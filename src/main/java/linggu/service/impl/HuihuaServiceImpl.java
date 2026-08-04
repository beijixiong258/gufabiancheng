package linggu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.Utils;
import linggu.entity.Huihua;
import linggu.mapper.HuihuaMapper;
import linggu.service.HuihuaService;
import linggu.service.JiluService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HuihuaServiceImpl extends ServiceImpl<HuihuaMapper, Huihua> implements HuihuaService {
    private final JiluService jiluService;
    @Override
    public String xinjian(String yonghuId, String jiluId) {
        jiluService.chakan(yonghuId, jiluId);
        String huihuaId=Utils.generateId();
        Huihua huihua=new Huihua(huihuaId,jiluId);
        if (!save(huihua)){
            throw new CommonException(500,"内部错误，会话创建失败。");
        }
        return huihuaId;
    }

    @Override
    public List<Huihua> chakanLiebiao(String yonghuId, String jiluId) {
        jiluService.chakan(yonghuId, jiluId);
        return list(new LambdaQueryWrapper<Huihua>().eq(Huihua::getJiluId,jiluId));
    }
}
