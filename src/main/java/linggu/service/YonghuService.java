package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.dto.ZhuceDTO;
import linggu.entity.Yonghu;

public interface YonghuService extends IService<Yonghu> {
    boolean zhuce(ZhuceDTO zhuceDTO);
}
