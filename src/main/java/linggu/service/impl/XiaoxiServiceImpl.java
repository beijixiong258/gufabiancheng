package linggu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import linggu.common.CommonException;
import linggu.common.Utils;
import linggu.entity.Xiaoxi;
import linggu.mapper.XiaoxiMapper;
import linggu.service.HuihuaService;
import linggu.service.XiaoxiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class XiaoxiServiceImpl extends ServiceImpl<XiaoxiMapper, Xiaoxi> implements XiaoxiService {
    private final HuihuaService huihuaService;
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
        return null;
    }
}
