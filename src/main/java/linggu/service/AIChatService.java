package linggu.service;

import cn.hutool.core.util.StrUtil;
import linggu.common.CommonException;
import linggu.common.ai.AIChatResponse;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
import linggu.entity.Xiaoxi;
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

    public boolean lianjie(){
        return StrUtil.isNotEmpty(chatClientBuilder.build().prompt()
                .user(CESHIXIAOXI)
                .call().content());
    }
    private boolean jiaoyan(AIChatResponse resp) {
        if (resp== null || resp.getZhuangtai()==null) {
            return false;
        }
        if (resp.getZhuangtai()==0) {
            return StrUtil.isNotBlank(resp.getYindao())
                    && StrUtil.isBlank(resp.getQuanwen());
        }
        if (resp.getZhuangtai()==1) {
            return StrUtil.isBlank(resp.getYindao())
                    && StrUtil.isNotBlank(resp.getQuanwen());
        }
        return false;
    }
    private List<Message> zuzhuang(List<Xiaoxi> xiaoxiList, Jilu jilu){
        String biaoqian = jilu.getBiaoqian();
        if (StrUtil.isBlank(biaoqian)){
            biaoqian = WUBIAOQIAN;
        }
        String zhengwen = jilu.getZhengwen();
        if (StrUtil.isBlank(zhengwen)){
            zhengwen = WUZHENGWEN;
        }
        String xitongTishici = XITONGTISHICI.formatted(jilu.getTimu(), jilu.getTicai(), biaoqian, zhengwen);
        List<Message> messageList = new ArrayList<>();
        messageList.add(new SystemMessage(xitongTishici));
        for (Xiaoxi xiaoxi: xiaoxiList) {
            String neirong = xiaoxi.getNeirong();
            if (xiaoxi.getLaiyuan()==1){
                messageList.add(new UserMessage(neirong));
            }
            else if (xiaoxi.getLaiyuan()==0) {
                messageList.add(new AssistantMessage(neirong));
            }
        }

        return messageList;
    }
    public Xiaoxi duihua(String yonghuId,String huihuaId,String neirong) {
        Huihua huihua = huihuaService.chakan(yonghuId, huihuaId);
        Jilu jilu = jiluService.chakan(yonghuId, huihua.getJiluId());
        xiaoxiService.xinjian(yonghuId, huihuaId, neirong, 1);
        List<Xiaoxi> xiaoxiList = xiaoxiService.chakanLiebiao(yonghuId, huihuaId);
        AIChatResponse resp;
        try {
            resp = chatClientBuilder.build().prompt().messages(this.zuzhuang(xiaoxiList, jilu))
                    .call()
                    .entity(AIChatResponse.class);
        } catch (Exception e) {
            throw new CommonException(500, "内部错误，AI回复获取失败");
        }
        if (!jiaoyan(resp)){
            throw new CommonException(500,"内部错误，AI回复获取失败。");
        }
        String huifu="";
        if (resp.getZhuangtai()==0){
            huifu= resp.getYindao();
        }
        else {
            huifu= resp.getQuanwen();
            return xiaoxiService.baocunAIHuifu(yonghuId,huihuaId,huifu);
        }
        return xiaoxiService.xinjian(yonghuId, huihuaId, huifu, 0);
    }
}
