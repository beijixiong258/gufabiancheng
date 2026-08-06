package linggu.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import linggu.common.CommonException;
import linggu.common.ai.AIChatResponse;
import linggu.dto.DuihuaDTO;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
import linggu.entity.Xiaoxi;
import linggu.enums.DuihuaZhuangtai;
import linggu.vo.DuihuaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static linggu.common.ai.Constant.*;

@Service
@RequiredArgsConstructor
public class AIChatService {
    private final ChatClient.Builder chatClientBuilder;
    private final XiaoxiService xiaoxiService;
    private final HuihuaService huihuaService;
    private final JiluService jiluService;
    private final ObjectMapper objectMapper;

    public boolean lianjie(){
        return StrUtil.isNotEmpty(chatClientBuilder.build().prompt()
                .user(CESHIXIAOXI)
                .call().content());
    }
    private boolean jsonJiaoyan(JsonNode jsonNode) {
        if (jsonNode==null || !jsonNode.isObject() || jsonNode.size() != 3) {
            return false;
        }
        if (!jsonNode.has("zhuangtai") || !jsonNode.has("yindao") || !jsonNode.has("quanwen")) {
            return false;
        }
        return jsonNode.get("zhuangtai").isInt()
                && (jsonNode.get("yindao").isNull() || jsonNode.get("yindao").isTextual())
                && (jsonNode.get("quanwen").isNull() || jsonNode.get("quanwen").isTextual());
    }
    private boolean jiaoyanAIHuida(AIChatResponse aiChatResponse) {
        if (aiChatResponse==null || aiChatResponse.getZhuangtai()==null) {
            return false;
        }
        if (aiChatResponse.getZhuangtai()== DuihuaZhuangtai.OVER.getCode()) {
            return StrUtil.isBlank(aiChatResponse.getYindao())
                    && StrUtil.isNotBlank(aiChatResponse.getQuanwen());
        }
        if (aiChatResponse.getZhuangtai()== DuihuaZhuangtai.CONTINUE.getCode()
                || aiChatResponse.getZhuangtai()== DuihuaZhuangtai.WAIT.getCode()) {
            return StrUtil.isNotBlank(aiChatResponse.getYindao())
                    && StrUtil.isBlank(aiChatResponse.getQuanwen());
        }
        return false;
    }
    private DuihuaZhuangtai zhuanhuanZhuangtai(Integer zhuangtaiCode) {
        for (DuihuaZhuangtai zhuangtai : DuihuaZhuangtai.values()) {
            if (zhuangtai.getCode()==zhuangtaiCode) {
                return zhuangtai;
            }
        }
        throw new CommonException(500,"内部错误，AI回复状态非法。");
    }
    private List<Message> zuzhuangAIXiaoxi(List<Xiaoxi> xiaoxiList,Jilu jilu,String zhiling,String zhengwen){
        String biaoqian = jilu.getBiaoqian();
        if (StrUtil.isBlank(biaoqian)){
            biaoqian = BIAOQIANQUESHI;
        }
        String xitongTishici = XITONGTISHICI.formatted(
                zhiling,
                jilu.getTimu(),
                jilu.getTicai(),
                biaoqian,
                zhengwen
        );
        List<Message> messageList = new ArrayList<>();
        messageList.add(new SystemMessage(xitongTishici));
        for (Xiaoxi xiaoxi: xiaoxiList) {
            String neirong = xiaoxi.getNeirong();
            if (xiaoxi.getLaiyuan()==1){
                messageList.add(new UserMessage(neirong));
            }
            else{
                messageList.add(new AssistantMessage(neirong));
            }
        }
        return messageList;
    }
    private AIChatResponse huoquAIHuida(List<Xiaoxi> xiaoxiList,Jilu jilu,String zhiling,String zhengwen) {
        try {
            String aiYuanshiHuida = chatClientBuilder.build().prompt()
                    .messages(this.zuzhuangAIXiaoxi(xiaoxiList,jilu,zhiling,zhengwen))
                    .call()
                    .content();
            JsonNode jsonNode = objectMapper.reader()
                    .with(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                    .readTree(aiYuanshiHuida);
            if (!jsonJiaoyan(jsonNode)) {
                throw new IllegalArgumentException("AI回复字段非法。");
            }
            AIChatResponse aiChatResponse = objectMapper.treeToValue(jsonNode,AIChatResponse.class);
            if (!jiaoyanAIHuida(aiChatResponse)){
                throw new IllegalArgumentException("AI回复字段组合非法。");
            }
            return aiChatResponse;
        } catch (Exception e) {
            throw new CommonException(500,"内部错误，AI回复获取失败。");
        }
    }

    private Xiaoxi baocunAIHuida(String yonghuId, String huihuaId, AIChatResponse aiChatResponse, DuihuaZhuangtai zhuangtai) {
        String neirong="";
        if (zhuangtai== DuihuaZhuangtai.OVER){
            neirong=aiChatResponse.getQuanwen();
        }
        else {
            neirong=aiChatResponse.getYindao();
        }
        return xiaoxiService.xinjian(yonghuId,huihuaId,neirong,0);
    }
    private DuihuaVO goujianDuihuaVO(Xiaoxi renleiXiaoxi, Xiaoxi aiHuida, DuihuaZhuangtai zhuangtai) {
        return new DuihuaVO().setRenleiXiaoxi(renleiXiaoxi).setAiHuida(aiHuida).setZhuangtai(zhuangtai);
    }
    public DuihuaVO duihua(String yonghuId,DuihuaDTO duihuaDTO) {
        Integer command=duihuaDTO.getCommand();
        if (command==null || command<0 || command>2) {
            throw new CommonException(400,"消息命令非法。");
        }
        if (command==0 && StrUtil.isBlank(duihuaDTO.getNeirong())) {
            throw new CommonException(400,"消息内容不能为空。");
        }

        String huihuaId=duihuaDTO.getHuihuaId();
        Huihua huihua=huihuaService.chakan(yonghuId,huihuaId);
        Jilu jilu=jiluService.chakan(yonghuId,huihua.getJiluId());
        Xiaoxi human=null;
        if (command==0) {
            human=xiaoxiService.xinjian(yonghuId,huihuaId,duihuaDTO.getNeirong(),1);
        }
        List<Xiaoxi> xiaoxiList = xiaoxiService.chakanLiebiao(yonghuId,huihuaId);
        String zhiling;
        if (command==0) {
            zhiling=DUIHUA;
        }
        else if (command==1) {
            zhiling=BUCHONG;
        }
        else if (command==2) {
            zhiling=JIESHU;
        }
        else {
            throw new CommonException(400,"消息命令非法。");
        }
        AIChatResponse aiChatResponse = huoquAIHuida(xiaoxiList,jilu,zhiling,duihuaDTO.getZhengwen());
        DuihuaZhuangtai zhuangtai = zhuanhuanZhuangtai(aiChatResponse.getZhuangtai());
        if (command==1 && zhuangtai!= DuihuaZhuangtai.CONTINUE) {
            throw new CommonException(500,"内部错误，AI未继续提问。");
        }
        else if (command==2 && zhuangtai!= DuihuaZhuangtai.OVER) {
            throw new CommonException(500,"内部错误，AI未生成正文。");
        }
        Xiaoxi ai = baocunAIHuida(yonghuId,huihuaId,aiChatResponse,zhuangtai);
        return goujianDuihuaVO(human,ai,zhuangtai);
    }
}
