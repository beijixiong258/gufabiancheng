package linggu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.LoginManager;
import linggu.common.Utils;
import linggu.dto.DengluDTO;
import linggu.dto.ZhuceDTO;
import linggu.entity.Yonghu;
import linggu.enums.Quanxian;
import linggu.mapper.YonghuMapper;
import linggu.service.YonghuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YonghuServiceImpl extends ServiceImpl<YonghuMapper, Yonghu> implements YonghuService {
    private final LoginManager loginManager;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean zhuce(ZhuceDTO zhuceDTO) {
        String zhanghao=zhuceDTO.getZhanghao();
        String mima= zhuceDTO.getMima();
        if (!list(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getZhanghao,zhanghao)).isEmpty()){
            throw new CommonException(400,"账号已存在。");
        }
        Yonghu yonghu=new Yonghu()
                .setId(Utils.generateId())
                .setZhanghao(zhanghao)
                .setMima(Utils.mimaJiami(mima))
                .setDianhua(zhuceDTO.getDianhua())
                .setShenfenzheng(zhuceDTO.getShenfenzheng())
                .setYouxiang(zhuceDTO.getYouxiang())
                .setQuanxian(Quanxian.USER);
        return (save(yonghu));
    }

    @Override
    public String denglu(DengluDTO dengluDTO) {
        String zhanghao=dengluDTO.getZhanghao();
        String mima= dengluDTO.getMima();
        List<Yonghu> yonghuList=list(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getZhanghao,zhanghao));
        if (yonghuList.isEmpty()){
            throw new CommonException(400,"登录账号不存在。");
        }
        Yonghu yonghu=yonghuList.get(0);
        if (!Utils.jiamiJiancha(mima,yonghu.getMima())){
            throw new CommonException(400,"密码错误。");
        }
        return loginManager.create(yonghu.getId());
    }
}
