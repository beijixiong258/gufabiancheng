package linggu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;

import linggu.entity.Xiaoxi;
import linggu.enums.XiaoxiLeixing;
import linggu.mapper.XiaoxiMapper;
import linggu.service.HuihuaService;
import linggu.service.XiaoxiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class XiaoxiServiceImpl extends ServiceImpl<XiaoxiMapper, Xiaoxi> implements XiaoxiService {
    private final HuihuaService huihuaService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Xiaoxi xinjian(String yonghuId, String huihuaId, String neirong, XiaoxiLeixing type) {
        if (type == null) {
            throw new CommonException(400, "消息来源非法。");
        }
        huihuaService.chakan(yonghuId, huihuaId);
        if (StrUtil.isBlank(neirong)){
            throw new CommonException(400,"消息内容非法。");
        }
        Xiaoxi xiaoxi = new Xiaoxi()
                .setHuihuaId(huihuaId)
                .setNeirong(neirong)
                .setType(type);
        if (!save(xiaoxi)){
            throw new CommonException(500,"内部错误，消息新建失败。");
        }
        Xiaoxi baocunJieguo = getById(xiaoxi.getId());
        if (baocunJieguo == null) {
            throw new CommonException(500, "内部错误，消息读取失败。");
        }
        return baocunJieguo;
    }
    @Override
    public List<Xiaoxi> chakanLiebiao(String yonghuId, String huihuaId) {
        huihuaService.chakan(yonghuId, huihuaId);
        return lambdaQuery()
                .eq(Xiaoxi::getHuihuaId,huihuaId)
                .orderByAsc(Xiaoxi::getId)
                .list();
    }

    @Override
    public boolean shanchu(String yonghuId, String huihuaId, Long xiaoxiId) {
        huihuaService.chakan(yonghuId,huihuaId);
        return lambdaUpdate()
                .eq(Xiaoxi::getId,xiaoxiId)
                .eq(Xiaoxi::getHuihuaId,huihuaId)
                .remove();
    }
}
