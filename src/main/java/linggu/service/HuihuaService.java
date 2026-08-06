package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.entity.Huihua;

import java.util.List;

public interface HuihuaService extends IService<Huihua> {
    String xinjian(String yonghuId,String jiluId);
    Huihua chakan(String yonghuId,String huihuaId);
    List<Huihua> chakanLiebiao(String yonghuId,String jiluId);
    boolean shanchu(String yonghuId,String huihuaId);
}
