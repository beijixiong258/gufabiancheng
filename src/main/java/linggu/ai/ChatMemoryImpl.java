package linggu.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import linggu.common.CommonException;
import linggu.entity.Xiaoxi;
import linggu.enums.XiaoxiLeixing;
import linggu.mapper.XiaoxiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMemoryImpl implements ChatMemory {
    private static final int RECENT_MAX = 12;
    private static final int SUMMARY_MAX = 4000;
    private final XiaoxiMapper xiaoxiMapper;

    private Message zhuanhuan(Xiaoxi xiaoxi) {
        return switch (xiaoxi.getType()) {
            case USER -> new UserMessage(xiaoxi.getNeirong());
            case ASSISTANT -> new AssistantMessage(xiaoxi.getNeirong());
            case SYSTEM -> new SystemMessage(xiaoxi.getNeirong());
            default -> throw new CommonException(500, "不支持此消息类型。");
        };
    }

    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            XiaoxiLeixing type;
            try {
                type = XiaoxiLeixing.valueOf(message.getMessageType().name());
            }
            catch (IllegalArgumentException e) {
                throw new CommonException(500, "不支持此消息类型。");
            }
            Xiaoxi xiaoxi = new Xiaoxi()
                    .setHuihuaId(conversationId)
                    .setType(type)
                    .setNeirong(message.getText());
            xiaoxiMapper.insert(xiaoxi);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Xiaoxi> list = xiaoxiMapper.selectList(new LambdaQueryWrapper<Xiaoxi>()
                .eq(Xiaoxi::getHuihuaId, conversationId)
                .orderByAsc(Xiaoxi::getId)
        );
        if (list.size() <= RECENT_MAX) {
            return list.stream().map(this::zhuanhuan).toList();
        }
        int summaryEnd = list.size() - RECENT_MAX;
        StringBuilder summary = new StringBuilder("历史摘要：");
        for (Xiaoxi message : list.subList(0, summaryEnd)) {
            String text = message.getNeirong() == null ? "" : message.getNeirong().replaceAll("\\s+", " ").trim();
            if (text.length() > 220) text = text.substring(0, 220) + "…";
            String line = "\n" + message.getType() + "：" + text;
            if (summary.length() + line.length() > SUMMARY_MAX) break;
            summary.append(line);
        }
        List<Message> result = new java.util.ArrayList<>();
        result.add(new SystemMessage(summary.toString()));
        result.addAll(list.subList(summaryEnd, list.size()).stream().map(this::zhuanhuan).toList());
        return result;
    }

    @Override
    @Transactional
    public void clear(String conversationId) {
        xiaoxiMapper.delete(new LambdaQueryWrapper<Xiaoxi>()
                .eq(Xiaoxi::getHuihuaId, conversationId));
    }
}
