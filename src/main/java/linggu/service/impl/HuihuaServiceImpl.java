package linggu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.entity.Huihua;
import linggu.mapper.HuihuaMapper;
import linggu.service.HuihuaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HuihuaServiceImpl extends ServiceImpl<HuihuaMapper, Huihua> implements HuihuaService {
}
