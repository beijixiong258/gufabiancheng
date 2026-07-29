package linggu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.Utils;
import linggu.dto.JiluXinjianDTO;
import linggu.entity.Jilu;
import linggu.enums.Zhuangtai;
import linggu.mapper.JiluMapper;
import linggu.service.JiluService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JiluServiceImpl extends ServiceImpl<JiluMapper, Jilu> implements JiluService {
    @Override
    public Jilu xinjian(String yonghuId, JiluXinjianDTO jiluXinjianDTO) {
        Jilu jilu=new Jilu();
        BeanUtil.copyProperties(jiluXinjianDTO,jilu);
        jilu.setId(Utils.generateId())
                .setYonghuId(yonghuId)
                .setZhuangtai(Zhuangtai.DRAFT);
        boolean success=save(jilu);
        if (!success){
            throw new CommonException(500,"内部错误，新建失败");
        }
        return getById(jilu.getId());
    }

    @Override
    public boolean xiugai(Jilu jilu) {
        return false;
    }

    @Override
    public Jilu chakan(String jiluId) {
        return null;
    }

    @Override
    public boolean shanchu(String jiluId) {
        return false;
    }

    @Override
    public boolean piliangShanchu(List<String> jiluIdList) {
        return false;
    }
}
