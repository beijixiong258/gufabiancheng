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

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMemoryImpl implements ChatMemory {
    private static final int MAX = 20;
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
                .orderByDesc(Xiaoxi::getId)
                .last("LIMIT " + MAX)
        );
        Collections.reverse(list);
        return list.stream().map(this::zhuanhuan).toList();
    }

    @Override
    @Transactional
    public void clear(String conversationId) {
        xiaoxiMapper.delete(new LambdaQueryWrapper<Xiaoxi>()
                .eq(Xiaoxi::getHuihuaId, conversationId));
    }
}
