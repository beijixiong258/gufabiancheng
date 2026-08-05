package linggu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.Utils;
import linggu.dto.JiluXinjianDTO;
import linggu.dto.JiluXiugaiDTO;
import linggu.entity.Jilu;
import linggu.enums.Zhuangtai;
import linggu.mapper.JiluMapper;
import linggu.service.JiluService;
import linggu.vo.JiluLiebiaoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean wancheng(String yonghuId, String jiluId) {
        Jilu jilu=getOne(new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
        );
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        if (StrUtil.isBlank(jilu.getZhengwen())){
            throw new CommonException(400, "正文为空，操作非法。");
        }
        jilu.setZhuangtai(Zhuangtai.FINISH);
        return updateById(jilu);
    }

    @Override
    public boolean xiugai(String yonghuId, String jiluId, JiluXiugaiDTO jiluXiugaiDTO) {
        Jilu jilu=getOne(new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
        );
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        BeanUtil.copyProperties(jiluXiugaiDTO,jilu);
        jilu.setZhuangtai(Zhuangtai.DRAFT);
        return updateById(jilu);
    }

    @Override
    public Jilu chakan(String yonghuId,String jiluId) {
        Jilu jilu=getOne(new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
        );
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        return jilu;
    }

    @Override
    public List<JiluLiebiaoVO> chakanLiebiao(String yonghuId) {
        List<Jilu> list=list(new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getYonghuId,yonghuId)
                .orderByDesc(Jilu::getXiugaiShijian)
        );
        return BeanUtil.copyToList(list, JiluLiebiaoVO.class);
    }

    @Override
    public boolean shanchu(String yonghuId,String jiluId) {
        Jilu jilu=getOne(new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
        );
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        return removeById(jilu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean piliangShanchu(String yonghuid,List<String> jiluIdList) {
        if (ObjectUtil.isEmpty(jiluIdList)){
            throw new CommonException(400,"ID列表为空。");
        }
        if (jiluIdList.stream().anyMatch(id -> StrUtil.isBlank(id))) {
            throw new CommonException(400, "存在为空的ID。");
        }
        List<String> list=jiluIdList.stream().distinct().toList();
        LambdaQueryWrapper<Jilu> lambdaQueryWrapper=new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getYonghuId,yonghuid)
                .in(Jilu::getId,list);
        if (count(lambdaQueryWrapper)!=list.size()){
            throw new CommonException(404,"部分记录不存在。");
        }
        return remove(lambdaQueryWrapper);
    }
}
