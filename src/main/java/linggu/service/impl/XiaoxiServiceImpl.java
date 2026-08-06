package linggu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.Utils;
import linggu.entity.Huihua;
import linggu.entity.Jilu;
import linggu.entity.Xiaoxi;
import linggu.mapper.XiaoxiMapper;
import linggu.service.HuihuaService;
import linggu.service.JiluService;
import linggu.service.XiaoxiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static linggu.enums.JiluZhuangtai.DRAFT;

@Service
@RequiredArgsConstructor
public class XiaoxiServiceImpl extends ServiceImpl<XiaoxiMapper, Xiaoxi> implements XiaoxiService {
    private final HuihuaService huihuaService;
    private final JiluService jiluService;
    @Override
    public Xiaoxi xinjian(String yonghuId, String huihuaId, String neirong,int laiyuan) {
        if ((laiyuan != 0) && (laiyuan != 1)){
            throw new CommonException(400, "消息来源非法。");
        }
        huihuaService.chakan(yonghuId, huihuaId);
        if (StrUtil.isBlank(neirong)){
            throw new CommonException(400,"消息内容非法。");
        }
        Xiaoxi zuihouXiaoxi=getOne(new LambdaQueryWrapper<Xiaoxi>()
                .eq(Xiaoxi::getHuihuaId,huihuaId)
                .orderByDesc(Xiaoxi::getXuhao)
                .last("LIMIT 1")
        );
        int xuhao=1;
        if (zuihouXiaoxi!=null){
            xuhao=zuihouXiaoxi.getXuhao()+1;
        }
        String xiaoxiId= Utils.generateId();
        Xiaoxi xiaoxi=new Xiaoxi().setId(xiaoxiId).setXuhao(xuhao).setNeirong(neirong).setLaiyuan(laiyuan).setHuihuaId(huihuaId);
        if (!save(xiaoxi)){
            throw new CommonException(500,"内部错误，消息新建失败。");
        }
        return getById(xiaoxiId);
    }
    @Override
    public List<Xiaoxi> chakanLiebiao(String yonghuId, String huihuaId) {
        huihuaService.chakan(yonghuId, huihuaId);
        return list(new LambdaQueryWrapper<Xiaoxi>()
                .eq(Xiaoxi::getHuihuaId,huihuaId)
                .orderByAsc(Xiaoxi::getXuhao)
        );
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Xiaoxi baocunAIShengchengJieguo(String yonghuId, String huihuaId, String zhengwen) {
        Huihua huihua=huihuaService.chakan(yonghuId, huihuaId);
        Xiaoxi xiaoxi=xinjian(yonghuId,huihuaId,zhengwen,0);

        boolean success=jiluService.update(new LambdaUpdateWrapper<Jilu>()
                .eq(Jilu::getId,huihua.getJiluId())
                .eq(Jilu::getYonghuId,yonghuId)
                .set(Jilu::getZhengwen,zhengwen)
                .set(Jilu::getJiluZhuangtai,DRAFT)
        );
        if (!success){
            throw new CommonException(500,"内部错误，正文更新失败。");
        }
        return xiaoxi;
    }
}
