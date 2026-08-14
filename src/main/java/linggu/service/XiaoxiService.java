package linggu.service;

import linggu.entity.Xiaoxi;

import java.util.List;

public interface XiaoxiService {
    Xiaoxi xinjian(String yonghuId, String huihuaId, String neirong, String type);
    List<Xiaoxi> chakanLiebiao(String yonghuId,String huihuaId);
}
