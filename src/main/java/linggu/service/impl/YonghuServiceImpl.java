package linggu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.LoginManager;
import linggu.common.Utils;
import linggu.dto.GuanliyuanXiugaiDTO;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuXiugaiDTO;
import linggu.dto.YonghuXinjianDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.entity.Yonghu;
import linggu.enums.Quanxian;
import linggu.mapper.YonghuMapper;
import linggu.service.YonghuService;
import linggu.vo.GuanliyuanChakanVO;
import linggu.vo.YonghuChakanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YonghuServiceImpl extends ServiceImpl<YonghuMapper, Yonghu> implements YonghuService {
    private final LoginManager loginManager;

    private boolean zhanghaoCunzai(String zhanghao) {
        return lambdaQuery()
                .eq(Yonghu::getZhanghao, zhanghao)
                .exists();
    }

    private boolean zhanghaoZhanyong(String zhanghao, String yonghuId) {
        return lambdaQuery()
                .eq(Yonghu::getZhanghao, zhanghao)
                .ne(Yonghu::getId, yonghuId)
                .exists();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean zhuce(YonghuZhuceDTO yonghuZhuceDTO) {
        String zhanghao = yonghuZhuceDTO.getZhanghao();
        String mima = yonghuZhuceDTO.getMima();
        if (zhanghaoCunzai(zhanghao)) {
            throw new CommonException(400, "账号已存在。");
        }
        Yonghu yonghu = new Yonghu()
                .setId(Utils.generateId())
                .setZhanghao(zhanghao)
                .setMima(Utils.mimaJiami(mima))
                .setDianhua(yonghuZhuceDTO.getDianhua())
                .setShenfenzheng(yonghuZhuceDTO.getShenfenzheng())
                .setYouxiang(yonghuZhuceDTO.getYouxiang())
                .setQuanxian(Quanxian.USER);
        return save(yonghu);
    }

    @Override
    public String denglu(YonghuDengluDTO yonghuDengluDTO) {
        String zhanghao = yonghuDengluDTO.getZhanghao();
        String mima = yonghuDengluDTO.getMima();
        List<Yonghu> yonghuList = lambdaQuery().eq(Yonghu::getZhanghao, zhanghao).list();
        if (yonghuList.isEmpty()) {
            throw new CommonException(400, "登录账号不存在。");
        }
        Yonghu yonghu = yonghuList.get(0);
        if (!Utils.jiamiJiancha(mima, yonghu.getMima())) {
            throw new CommonException(400, "密码错误。");
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
        Yonghu yonghu = getById(yonghuId);
        if (yonghu == null) {
            throw new CommonException(404, "个人信息加载失败，未找到指定用户。");
        }
        return new YonghuChakanVO()
                .setId(yonghu.getId())
                .setZhanghao(yonghu.getZhanghao())
                .setDianhua(yonghu.getDianhua())
                .setYouxiang(yonghu.getYouxiang());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xiugai(String yonghuId, YonghuXiugaiDTO yonghuXiugaiDTO) {
        Yonghu yonghu = getById(yonghuId);
        if (yonghu == null) {
            throw new CommonException(404, "修改失败，未找到指定用户。");
        }
        if (zhanghaoZhanyong(yonghuXiugaiDTO.getZhanghao(), yonghuId)) {
            throw new CommonException(400, "账号重复");
        }
        return lambdaUpdate()
                .eq(Yonghu::getId, yonghuId)
                .set(Yonghu::getZhanghao, yonghuXiugaiDTO.getZhanghao())
                .set(yonghuXiugaiDTO.getDianhua() != null, Yonghu::getDianhua, yonghuXiugaiDTO.getDianhua())
                .set(yonghuXiugaiDTO.getYouxiang() != null, Yonghu::getYouxiang, yonghuXiugaiDTO.getYouxiang())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xiugaiMima(String yonghuId, String mima1, String mima2) {
        Yonghu yonghu = getById(yonghuId);
        if (yonghu == null) {
            throw new CommonException(404, "修改失败，未找到指定用户。");
        }
        if (!Utils.jiamiJiancha(mima1, yonghu.getMima())) {
            throw new CommonException(400, "原密码输入错误。");
        }
        return lambdaUpdate()
                .eq(Yonghu::getId, yonghuId)
                .set(Yonghu::getMima, Utils.mimaJiami(mima2))
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xinjian(YonghuXinjianDTO yonghuXinjianDTO) {
        Yonghu yonghu = new Yonghu();
        BeanUtil.copyProperties(yonghuXinjianDTO, yonghu, "mima");
        String zhanghao = yonghu.getZhanghao();
        if (zhanghaoCunzai(zhanghao)) {
            throw new CommonException(400, "账号已存在。");
        }
        yonghu.setId(Utils.generateId())
                .setMima(Utils.mimaJiami(yonghuXinjianDTO.getMima()));
        return save(yonghu);
    }

    @Override
    public GuanliyuanChakanVO chakanYonghu(String yonghuId) {
        Yonghu yonghu = getById(yonghuId);
        if (yonghu == null) {
            throw new CommonException(404, "用户ID不存在。");
        }
        GuanliyuanChakanVO vo = new GuanliyuanChakanVO();
        BeanUtil.copyProperties(yonghu, vo);
        return vo;
    }

    @Override
    public List<GuanliyuanChakanVO> chakanLiebiao() {
        return BeanUtil.copyToList(list(), GuanliyuanChakanVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xiugai(GuanliyuanXiugaiDTO guanliyuanXiugaiDTO) {
        String yonghuId = guanliyuanXiugaiDTO.getId();
        if (getById(yonghuId) == null) {
            throw new CommonException(404, "用户不存在。");
        }
        String zhanghao = guanliyuanXiugaiDTO.getZhanghao();
        if (zhanghaoZhanyong(zhanghao, yonghuId)) {
            throw new CommonException(400, "账号重复");
        }
        String mima = guanliyuanXiugaiDTO.getMima();
        boolean xuyaoXiugaiMima = mima != null && !mima.isBlank();
        String jiamiMima = xuyaoXiugaiMima ? Utils.mimaJiami(mima) : null;
        return lambdaUpdate()
                .eq(Yonghu::getId, yonghuId)
                .set(Yonghu::getZhanghao, zhanghao)
                .set(guanliyuanXiugaiDTO.getDianhua() != null, Yonghu::getDianhua, guanliyuanXiugaiDTO.getDianhua())
                .set(guanliyuanXiugaiDTO.getShenfenzheng() != null, Yonghu::getShenfenzheng, guanliyuanXiugaiDTO.getShenfenzheng())
                .set(guanliyuanXiugaiDTO.getYouxiang() != null, Yonghu::getYouxiang, guanliyuanXiugaiDTO.getYouxiang())
                .set(Yonghu::getQuanxian, guanliyuanXiugaiDTO.getQuanxian())
                .set(xuyaoXiugaiMima, Yonghu::getMima, jiamiMima)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shanchu(String yonghuId) {
        if (getById(yonghuId) == null) {
            throw new CommonException(404, "删除失败，用户不存在。");
        }
        return removeById(yonghuId);
    }
}
