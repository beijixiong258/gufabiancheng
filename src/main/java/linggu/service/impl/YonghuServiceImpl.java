package linggu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.LoginManager;
import linggu.common.Utils;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuGengxinDTO;
import linggu.dto.YonghuXinzengDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.entity.Yonghu;
import linggu.enums.Quanxian;
import linggu.mapper.YonghuMapper;
import linggu.service.YonghuService;
import linggu.vo.GuanliyuanChaxunVO;
import linggu.vo.YonghuChakanVO;
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
    public boolean zhuce(YonghuZhuceDTO yonghuZhuceDTO) {
        String zhanghao= yonghuZhuceDTO.getZhanghao();
        String mima= yonghuZhuceDTO.getMima();
        if (!list(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getZhanghao,zhanghao)).isEmpty()){
            throw new CommonException(400,"账号已存在。");
        }
        Yonghu yonghu=new Yonghu()
                .setId(Utils.generateId())
                .setZhanghao(zhanghao)
                .setMima(Utils.mimaJiami(mima))
                .setDianhua(yonghuZhuceDTO.getDianhua())
                .setShenfenzheng(yonghuZhuceDTO.getShenfenzheng())
                .setYouxiang(yonghuZhuceDTO.getYouxiang())
                .setQuanxian(Quanxian.USER);
        return (save(yonghu));
    }
    @Override
    public String denglu(YonghuDengluDTO yonghuDengluDTO) {
        String zhanghao= yonghuDengluDTO.getZhanghao();
        String mima= yonghuDengluDTO.getMima();
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

    @Override
    public String shuaxin(String token) {
        return loginManager.refresh(token);
    }

    @Override
    public boolean tuichu(String token) {
        loginManager.remove(token);
        return true;
    }

    @Override
    public YonghuChakanVO chakan(String yonghuId) {
        Yonghu yonghu=getById(yonghuId);
        if (yonghu==null){
            throw new CommonException(404,"个人信息加载失败，未找到指定用户。");
        }
        return new YonghuChakanVO()
                .setId(yonghu.getId())
                .setZhanghao(yonghu.getZhanghao())
                .setDianhua(yonghu.getDianhua())
                .setYouxiang(yonghu.getYouxiang());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gengxin(String id, YonghuGengxinDTO yonghuGengxinDTO) {
        Yonghu yonghu=getById(id);
        if (yonghu==null){
            throw new CommonException(404,"更新失败，未找到指定用户。");
        }
        LambdaQueryWrapper<Yonghu> lambdaQueryWrapper=new LambdaQueryWrapper<Yonghu>()
                .eq(Yonghu::getZhanghao,yonghuGengxinDTO.getZhanghao())
                .ne(Yonghu::getId,id);
        if (!list(lambdaQueryWrapper).isEmpty()){
            throw new CommonException(400,"账号重复");
        }
        yonghu.setZhanghao(yonghuGengxinDTO.getZhanghao())
                .setDianhua(yonghuGengxinDTO.getDianhua())
                .setYouxiang(yonghuGengxinDTO.getYouxiang());
        return (updateById(yonghu));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xiugaiMima(String id, String mima1,String mima2) {
        Yonghu yonghu=getById(id);
        if (yonghu==null){
            throw new CommonException(404,"更新失败，未找到指定用户。");
        }
        if (!Utils.jiamiJiancha(mima1,yonghu.getMima())){
            throw new CommonException(400,"原密码输入错误。");
        }
        LambdaUpdateWrapper<Yonghu> lambdaUpdateWrapper=new LambdaUpdateWrapper<Yonghu>()
                .eq(Yonghu::getId,id)
                .set(Yonghu::getMima,Utils.mimaJiami(mima2));
        return (update(lambdaUpdateWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xinzeng(YonghuXinzengDTO yonghuXinzengDTO) {
        Yonghu yonghu= new Yonghu();
        BeanUtil.copyProperties(yonghuXinzengDTO,yonghu,"mima");
        String zhanghao=yonghu.getZhanghao();
        LambdaQueryWrapper<Yonghu> lambdaQueryWrapper=new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getZhanghao,zhanghao);
        if (!list(lambdaQueryWrapper).isEmpty()){
            throw new CommonException(400,"账号已存在。");
        }
        yonghu.setId(Utils.generateId()).setMima(Utils.mimaJiami(yonghuXinzengDTO.getMima()));
        return (save(yonghu));
    }


    @Override
    public GuanliyuanChaxunVO chaxun(String yonghuId) {
        Yonghu yonghu=getById(yonghuId);
        if (yonghu==null){
            throw new CommonException(404,"用户ID不存在。");
        }
        GuanliyuanChaxunVO vo=new GuanliyuanChaxunVO();
        BeanUtil.copyProperties(yonghu,vo);
        return vo;
    }

    @Override
    public List<GuanliyuanChaxunVO> huoquLiebiao() {
        List<Yonghu> yonghuList=list();
        return BeanUtil.copyToList(yonghuList, GuanliyuanChaxunVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xiugai(Yonghu yonghu) {
        if (getById(yonghu.getId())==null){
            throw new CommonException(404,"用户不存在。");
        }
        String zhanghao=yonghu.getZhanghao();
        LambdaQueryWrapper<Yonghu> lambdaQueryWrapper=new LambdaQueryWrapper<Yonghu>()
                .eq(Yonghu::getZhanghao,zhanghao)
                .ne(Yonghu::getId,yonghu.getId());
        if (!list(lambdaQueryWrapper).isEmpty()){
            throw new CommonException(400,"账号重复");
        }
        String mima = yonghu.getMima();
        if (mima==null || mima.isBlank()) {
            mima = "000000";
        }
        yonghu.setMima(Utils.mimaJiami(mima));
        return (updateById(yonghu));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shanchu(String yonghuId) {
        //待完成删除用户创建的记录
        if (getById(yonghuId)==null){
            throw new CommonException(404,"删除失败，用户不存在。");
        }
        return (removeById(yonghuId));
    }
}
