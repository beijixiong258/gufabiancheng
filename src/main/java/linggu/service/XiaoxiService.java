package linggu.service;

import linggu.entity.Xiaoxi;

import java.util.List;

public interface XiaoxiService {
    Xiaoxi xinjian(String yonghuId,String huihuaId,String neirong,int laiyuan);
    List<Xiaoxi> chakanLiebiao(String yonghuId,String huihuaId);
    Xiaoxi baocunAIHuifu(String yonghuId,String huihuaId,String huifu);
}
