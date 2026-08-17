package linggu.ai;

import cn.hutool.core.util.StrUtil;
import linggu.common.CommonException;
import linggu.dto.DuihuaDTO;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
import linggu.entity.Xiaoxi;
import linggu.enums.DuihuaZhuangtai;
import linggu.enums.XiaoxiLeixing;
import linggu.service.HuihuaService;
import linggu.service.JiluService;
import linggu.service.XiaoxiService;
import linggu.vo.DuihuaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static linggu.ai.AIConstant.BIAOQIANQUESHI;
import static linggu.ai.AIConstant.BUCHONG;
import static linggu.ai.AIConstant.CESHIXIAOXI;
import static linggu.ai.AIConstant.DUIHUA;
import static linggu.ai.AIConstant.JIESHU;
import static linggu.ai.AIConstant.XITONGTISHICI;

@Service
@RequiredArgsConstructor
public class AIChatService {
    private final ChatClient.Builder chatClientBuilder;
    private final XiaoxiService xiaoxiService;
    private final HuihuaService huihuaService;
    private final JiluService jiluService;
    private final ChatMemory chatMemory;
    private final AIResponseParser aiResponseParser;
    private final ConcurrentHashMap<String, Object> concurrentHashMap = new ConcurrentHashMap<>();

    public boolean lianjie() {
        return StrUtil.isNotBlank(chatClientBuilder.build().prompt()
                .user(CESHIXIAOXI)
                .call().content());
    }

    private List<Message> zuzhuangAIXiaoxi(List<Message> messageList, Jilu jilu, String zhiling, String zhengwen) {
        String biaoqian = jilu.getBiaoqian();
        if (StrUtil.isBlank(biaoqian)) {
            biaoqian = BIAOQIANQUESHI;
        }
        String xitongTishici = XITONGTISHICI.formatted(
                zhiling,
                jilu.getTimu(),
                jilu.getTicai(),
                biaoqian,
                zhengwen
        );
        List<Message> list = new ArrayList<>();
        list.add(new SystemMessage(xitongTishici));
        list.addAll(messageList);
        return list;
    }

    private AIChatResponse huoquAIHuida(List<Message> messageList, Jilu jilu, String zhiling, String zhengwen) {
        String aiYuanshiHuida;
        try {
            aiYuanshiHuida = chatClientBuilder.build().prompt()
                    .messages(zuzhuangAIXiaoxi(messageList, jilu, zhiling, zhengwen))
                    .call()
                    .content();
        }
        catch (Exception e) {
            throw new CommonException(500, "内部错误，AI回复获取失败。");
        }
        return aiResponseParser.jiexi(aiYuanshiHuida);
    }

    private Xiaoxi baocunAIHuida(
            String yonghuId,
            String huihuaId,
            AIChatResponse aiChatResponse,
            DuihuaZhuangtai zhuangtai
    ) {
        String neirong;
        if (zhuangtai == DuihuaZhuangtai.OVER) {
            neirong = aiChatResponse.getQuanwen();
        }
        else {
            neirong = aiChatResponse.getYindao();
        }
        return xiaoxiService.xinjian(yonghuId, huihuaId, neirong, XiaoxiLeixing.ASSISTANT);
    }

    private DuihuaVO goujianDuihuaVO(Xiaoxi renleiXiaoxi, Xiaoxi aiHuida, DuihuaZhuangtai zhuangtai) {
        return new DuihuaVO()
                .setRenleiXiaoxi(renleiXiaoxi)
                .setAiHuida(aiHuida)
                .setZhuangtai(zhuangtai);
    }

    public DuihuaVO duihua(String yonghuId, DuihuaDTO duihuaDTO) {
        Integer command = duihuaDTO.getCommand();
        if (command == null || command < 0 || command > 2) {
            throw new CommonException(400, "消息命令非法。");
        }
        if (command == 0 && StrUtil.isBlank(duihuaDTO.getNeirong())) {
            throw new CommonException(400, "消息内容不能为空。");
        }

        String huihuaId = duihuaDTO.getHuihuaId();
        Huihua huihua = huihuaService.chakan(yonghuId, huihuaId);
        Object lock = concurrentHashMap.computeIfAbsent(huihuaId, id -> new Object());
        synchronized (lock) {
            Jilu jilu = jiluService.chakan(yonghuId, huihua.getJiluId());
            Xiaoxi human = null;
            try {
                if (command == 0) {
                    human = xiaoxiService.xinjian(
                            yonghuId,
                            huihuaId,
                            duihuaDTO.getNeirong(),
                            XiaoxiLeixing.USER
                    );
                }
                List<Message> messageList = chatMemory.get(huihuaId);
                String zhiling;
                if (command == 0) {
                    zhiling = DUIHUA;
                }
                else if (command == 1) {
                    zhiling = BUCHONG;
                }
                else {
                    zhiling = JIESHU;
                }
                AIChatResponse aiChatResponse = huoquAIHuida(
                        messageList,
                        jilu,
                        zhiling,
                        duihuaDTO.getZhengwen()
                );
                DuihuaZhuangtai zhuangtai = aiResponseParser.zhuanhuan(
                        aiChatResponse.getZhuangtai()
                );
                if (command == 1 && zhuangtai != DuihuaZhuangtai.CONTINUE) {
                    throw new CommonException(500, "内部错误，AI未继续提问。");
                }
                if (command == 2 && zhuangtai != DuihuaZhuangtai.OVER) {
                    throw new CommonException(500, "内部错误，AI未生成正文。");
                }
                Xiaoxi ai = baocunAIHuida(yonghuId, huihuaId, aiChatResponse, zhuangtai);
                return goujianDuihuaVO(human, ai, zhuangtai);
            }
            catch (RuntimeException e) {
                if (human != null) {
                    try {
                        boolean success = xiaoxiService.shanchu(yonghuId, huihuaId, human.getId());
                        if (!success) {
                            e.addSuppressed(new IllegalStateException("失败消息清理失败。"));
                        }
                    }
                    catch (RuntimeException shanchuException) {
                        e.addSuppressed(shanchuException);
                    }
                }
                throw e;
            }
        }
    }
}
