package linggu.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import linggu.common.CommonException;
import linggu.common.ai.AIChatResponse;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
import linggu.entity.Xiaoxi;
import linggu.enums.HuihuaZhuangtai;
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
                .user(CESHI_XIAOXI)
                .call().content());
    }

    private boolean jiaoyanJsonJiegou(JsonNode jsonNode) {
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
        if (aiChatResponse.getZhuangtai()==HuihuaZhuangtai.OVER.getCode()) {
            return StrUtil.isBlank(aiChatResponse.getYindao())
                    && StrUtil.isNotBlank(aiChatResponse.getQuanwen());
        }
        if (aiChatResponse.getZhuangtai()==HuihuaZhuangtai.CONTINUE.getCode()
                || aiChatResponse.getZhuangtai()==HuihuaZhuangtai.WAIT.getCode()) {
            return StrUtil.isNotBlank(aiChatResponse.getYindao())
                    && StrUtil.isBlank(aiChatResponse.getQuanwen());
        }
        return false;
    }
    private HuihuaZhuangtai zhuanhuanZhuangtai(Integer zhuangtaiCode) {
        for (HuihuaZhuangtai zhuangtai : HuihuaZhuangtai.values()) {
            if (zhuangtai.getCode()==zhuangtaiCode) {
                return zhuangtai;
            }
        }
        throw new CommonException(500,"内部错误，AI回复状态非法。");
    }
    private int tongjiWendaLunshu(List<Xiaoxi> xiaoxiList) {
        long renleiXiaoxiShu = xiaoxiList.stream()
                .filter(xiaoxi -> xiaoxi.getLaiyuan()==1)
                .count();
        return Math.max(0,(int)renleiXiaoxiShu-1);
    }
    private String huoquPutongZhiling(int wendaLunshu) {
        if (wendaLunshu<5) {
            return BUZU_WULUN_ZHILING;
        }
        if (wendaLunshu<8) {
            return WU_DAO_QILUN_ZHILING;
        }
        return DADAO_BALUN_ZHILING;
    }
    private void jiaoyanPutongDuihuaZhuangtai(HuihuaZhuangtai zhuangtai, int wendaLunshu) {
        if (wendaLunshu<5 && zhuangtai!=HuihuaZhuangtai.CONTINUE) {
            throw new CommonException(500,"内部错误，AI未按问答轮数继续追问。");
        }
        if (wendaLunshu>=5 && wendaLunshu<8 && zhuangtai==HuihuaZhuangtai.OVER) {
            throw new CommonException(500,"内部错误，AI未等待用户选择。");
        }
        if (wendaLunshu>=8 && zhuangtai!=HuihuaZhuangtai.OVER) {
            throw new CommonException(500,"内部错误，AI未按问答轮数生成正文。");
        }
    }

    private List<Message> zuzhuangAIXiaoxi(List<Xiaoxi> xiaoxiList,Jilu jilu,int wendaLunshu,String zhiling){
        String biaoqian = jilu.getBiaoqian();
        if (StrUtil.isBlank(biaoqian)){
            biaoqian = WU_BIAOQIAN;
        }
        String zhengwen = jilu.getZhengwen();
        if (StrUtil.isBlank(zhengwen)){
            zhengwen = WU_ZHENGWEN;
        }
        String xitongTishici = XITONG_TISHICI.formatted(
                wendaLunshu,
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
    private AIChatResponse huoquAIHuida(List<Xiaoxi> xiaoxiList,Jilu jilu,int wendaLunshu,String zhiling) {
        try {
            String aiYuanshiHuida = chatClientBuilder.build().prompt()
                    .messages(this.zuzhuangAIXiaoxi(xiaoxiList,jilu,wendaLunshu,zhiling))
                    .call()
                    .content();
            JsonNode jsonNode = objectMapper.reader()
                    .with(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                    .readTree(aiYuanshiHuida);
            if (!jiaoyanJsonJiegou(jsonNode)) {
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

    private Xiaoxi baocunAIHuida(String yonghuId,String huihuaId,AIChatResponse aiChatResponse,HuihuaZhuangtai zhuangtai) {
        if (zhuangtai==HuihuaZhuangtai.OVER){
            return xiaoxiService.baocunAIShengchengJieguo(yonghuId,huihuaId,aiChatResponse.getQuanwen());
        }
        return xiaoxiService.xinjian(yonghuId,huihuaId,aiChatResponse.getYindao(),0);
    }

    private DuihuaVO goujianDuihuaVO(Xiaoxi renleiXiaoxi,Xiaoxi aiHuida,HuihuaZhuangtai zhuangtai) {
        return new DuihuaVO().setRenleiXiaoxi(renleiXiaoxi).setAiHuida(aiHuida).setZhuangtai(zhuangtai);
    }

    public DuihuaVO duihua(String yonghuId,String huihuaId,String neirong) {
        Huihua huihua = huihuaService.chakan(yonghuId,huihuaId);
        Jilu jilu = jiluService.chakan(yonghuId,huihua.getJiluId());
        Xiaoxi renleiXiaoxi = xiaoxiService.xinjian(yonghuId,huihuaId,neirong,1);
        List<Xiaoxi> xiaoxiList = xiaoxiService.chakanLiebiao(yonghuId,huihuaId);
        int wendaLunshu = tongjiWendaLunshu(xiaoxiList);
        AIChatResponse aiChatResponse = huoquAIHuida(xiaoxiList,jilu,wendaLunshu,huoquPutongZhiling(wendaLunshu));
        HuihuaZhuangtai zhuangtai = zhuanhuanZhuangtai(aiChatResponse.getZhuangtai());
        jiaoyanPutongDuihuaZhuangtai(zhuangtai,wendaLunshu);
        Xiaoxi aiHuida = baocunAIHuida(yonghuId,huihuaId,aiChatResponse,zhuangtai);
        return goujianDuihuaVO(renleiXiaoxi,aiHuida,zhuangtai);
    }

    public DuihuaVO jixuYilun(String yonghuId,String huihuaId) {
        Huihua huihua = huihuaService.chakan(yonghuId,huihuaId);
        Jilu jilu = jiluService.chakan(yonghuId,huihua.getJiluId());
        List<Xiaoxi> xiaoxiList = xiaoxiService.chakanLiebiao(yonghuId,huihuaId);
        int wendaLunshu = tongjiWendaLunshu(xiaoxiList);
        AIChatResponse aiChatResponse = huoquAIHuida(xiaoxiList,jilu,wendaLunshu,JIXU_YILUN_ZHILING);
        HuihuaZhuangtai zhuangtai = zhuanhuanZhuangtai(aiChatResponse.getZhuangtai());
        if (zhuangtai!=HuihuaZhuangtai.CONTINUE) {
            throw new CommonException(500,"内部错误，AI未继续提问。");
        }
        Xiaoxi aiHuida = baocunAIHuida(yonghuId,huihuaId,aiChatResponse,zhuangtai);
        return goujianDuihuaVO(null,aiHuida,zhuangtai);
    }

    public DuihuaVO shengchengZhengwen(String yonghuId, String huihuaId) {
        Huihua huihua = huihuaService.chakan(yonghuId,huihuaId);
        Jilu jilu = jiluService.chakan(yonghuId,huihua.getJiluId());
        List<Xiaoxi> xiaoxiList = xiaoxiService.chakanLiebiao(yonghuId,huihuaId);
        int wendaLunshu = tongjiWendaLunshu(xiaoxiList);
        AIChatResponse aiChatResponse = huoquAIHuida(xiaoxiList,jilu,wendaLunshu,SHENGCHENG_ZHENGWEN_ZHILING);
        HuihuaZhuangtai zhuangtai = zhuanhuanZhuangtai(aiChatResponse.getZhuangtai());
        if (zhuangtai!=HuihuaZhuangtai.OVER) {
            throw new CommonException(500,"内部错误，AI未生成正文。");
        }
        Xiaoxi aiHuida = baocunAIHuida(yonghuId,huihuaId,aiChatResponse,zhuangtai);
        return goujianDuihuaVO(null,aiHuida,zhuangtai);
    }
}
