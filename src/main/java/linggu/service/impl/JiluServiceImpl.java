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
import linggu.enums.JiluZhuangtai;
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
                .setJiluZhuangtai(JiluZhuangtai.DRAFT);
        boolean success=save(jilu);
        if (!success){
            throw new CommonException(500,"内部错误，新建失败");
        }
        return getById(jilu.getId());
    }

    @Override
    public boolean wancheng(String yonghuId, String jiluId) {
        Jilu jilu=lambdaQuery()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
                .one();
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        if (StrUtil.isBlank(jilu.getZhengwen())){
            throw new CommonException(400, "正文为空，操作非法。");
        }
        return lambdaUpdate()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
                .isNotNull(Jilu::getZhengwen)
                .apply("TRIM(zhengwen) <> ''")
                .set(Jilu::getJiluZhuangtai,JiluZhuangtai.FINISH)
                .update();
    }

    @Override
    public boolean xiugai(String yonghuId, String jiluId, JiluXiugaiDTO jiluXiugaiDTO) {
        Jilu jilu=lambdaQuery()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
                .one();
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        return lambdaUpdate()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
                .set(Jilu::getTimu,jiluXiugaiDTO.getTimu())
                .set(Jilu::getTicai,jiluXiugaiDTO.getTicai())
                .set(jiluXiugaiDTO.getBiaoqian()!=null,Jilu::getBiaoqian,jiluXiugaiDTO.getBiaoqian())
                .set(jiluXiugaiDTO.getZhengwen()!=null,Jilu::getZhengwen,jiluXiugaiDTO.getZhengwen())
                .set(Jilu::getJiluZhuangtai,JiluZhuangtai.DRAFT)
                .update();
    }

    @Override
    public Jilu chakan(String yonghuId,String jiluId) {
        Jilu jilu=lambdaQuery()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
                .one();
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        return jilu;
    }

    @Override
    public List<JiluLiebiaoVO> chakanLiebiao(String yonghuId) {
        List<Jilu> jiluList=lambdaQuery()
                .eq(Jilu::getYonghuId,yonghuId)
                .orderByDesc(Jilu::getXiugaiShijian)
                .list();
        return BeanUtil.copyToList(jiluList, JiluLiebiaoVO.class);
    }

    @Override
    public boolean shanchu(String yonghuId,String jiluId) {
        Jilu jilu=lambdaQuery()
                .eq(Jilu::getId,jiluId)
                .eq(Jilu::getYonghuId,yonghuId)
                .one();
        if (jilu==null){
            throw new CommonException(404,"记录不存在。");
        }
        return removeById(jilu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean piliangShanchu(String yonghuId,List<String> jiluIdList) {
        if (ObjectUtil.isEmpty(jiluIdList)){
            throw new CommonException(400,"ID列表为空。");
        }
        if (jiluIdList.stream().anyMatch(id -> StrUtil.isBlank(id))) {
            throw new CommonException(400, "存在为空的ID。");
        }
        List<String> quchongJiluIdList=jiluIdList.stream().distinct().toList();
        LambdaQueryWrapper<Jilu> jiluQueryWrapper=new LambdaQueryWrapper<Jilu>()
                .eq(Jilu::getYonghuId,yonghuId)
                .in(Jilu::getId,quchongJiluIdList);
        if (count(jiluQueryWrapper)!=quchongJiluIdList.size()){
            throw new CommonException(404,"部分记录不存在。");
        }
        return remove(jiluQueryWrapper);
    }
}
