package linggu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.dto.ZhuceDTO;
import linggu.entity.Yonghu;
import linggu.mapper.YonghuMapper;
import linggu.service.YonghuService;
import org.springframework.stereotype.Service;

@Service
public class YonghuServiceImpl extends ServiceImpl<YonghuMapper, Yonghu> implements YonghuService {
    @Override
    public boolean zhuce(ZhuceDTO zhuceDTO) {
        return false;
    }
}
