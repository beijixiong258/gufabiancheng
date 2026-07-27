package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.dto.DengluDTO;
import linggu.dto.ZhuceDTO;
import linggu.entity.Yonghu;

public interface YonghuService extends IService<Yonghu> {
    boolean zhuce(ZhuceDTO zhuceDTO);//新用户注册，默认权限为普通用户
    String denglu(DengluDTO dengluDTO);//用户登录，返回token
}
