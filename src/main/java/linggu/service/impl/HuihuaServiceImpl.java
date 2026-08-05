package linggu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.Utils;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
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
            throw new CommonException(500,"内部错误，新建失败。");
        }
        return huihuaId;
    }

    @Override
    public Huihua chakan(String yonghuId, String huihuaId) {
        Huihua huihua=getById(huihuaId);
        if (huihua==null){
            throw new CommonException(404,"会话不存在。");
        }
        Jilu jilu=jiluService.getOne(new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getYonghuId,yonghuId)
                .eq(Jilu::getId,huihua.getJiluId())
        );
        if (jilu==null){
            throw new CommonException(404,"会话不存在");
        }
        return huihua;
    }

    @Override
    public List<Huihua> chakanLiebiao(String yonghuId, String jiluId) {
        jiluService.chakan(yonghuId, jiluId);
        return list(new LambdaQueryWrapper<Huihua>().eq(Huihua::getJiluId,jiluId));
    }
}
