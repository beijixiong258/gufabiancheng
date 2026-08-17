package linggu.service;

import linggu.entity.Xiaoxi;
import linggu.enums.XiaoxiLeixing;

import java.util.List;

public interface XiaoxiService {
    Xiaoxi xinjian(String yonghuId, String huihuaId, String neirong, XiaoxiLeixing type);
    List<Xiaoxi> chakanLiebiao(String yonghuId,String huihuaId);
    boolean shanchu(String yonghuId,String huihuaId,Long xiaoxiId);
}
