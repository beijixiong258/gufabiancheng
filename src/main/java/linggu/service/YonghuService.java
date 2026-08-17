package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.dto.GuanliyuanXiugaiDTO;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuXiugaiDTO;
import linggu.dto.YonghuXinjianDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.entity.Yonghu;
import linggu.vo.GuanliyuanChakanVO;
import linggu.vo.YonghuChakanVO;

import java.util.List;

public interface YonghuService extends IService<Yonghu> {
    boolean zhuce(YonghuZhuceDTO yonghuZhuceDTO);
    String denglu(YonghuDengluDTO yonghuDengluDTO);
    String shuaxin(String token);
    boolean tuichu(String token);
    YonghuChakanVO chakan(String yonghuId);
    boolean xiugai(String yonghuId, YonghuXiugaiDTO yonghuXiugaiDTO);
    boolean xiugaiMima(String yonghuId, String mima1, String mima2);

    boolean xinjian(YonghuXinjianDTO yonghuXinjianDTO);
    GuanliyuanChakanVO chakanYonghu(String yonghuId);
    List<GuanliyuanChakanVO> chakanLiebiao();
    boolean xiugai(GuanliyuanXiugaiDTO guanliyuanXiugaiDTO);
    boolean shanchu(String yonghuId);
}
