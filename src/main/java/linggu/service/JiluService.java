package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.common.PageResult;
import linggu.dto.JiluXinjianDTO;
import linggu.dto.JiluXiugaiDTO;
import linggu.entity.Jilu;
import linggu.enums.JiluZhuangtai;
import linggu.enums.Ticai;
import linggu.vo.JiluLiebiaoVO;

import java.util.List;

public interface JiluService extends IService<Jilu> {
    Jilu xinjian(String yonghuId,JiluXinjianDTO jiluXinjianDTO);

    boolean wancheng(String yonghuId,String jiluId);

    boolean xiugai(String yonghuId, String jiluId, JiluXiugaiDTO jiluXiugaiDTO);

    Jilu chakan(String yonghuId, String jiluId);
    PageResult<JiluLiebiaoVO> chakanLiebiao(
            String yonghuId,
            String guanjianci,
            Ticai ticai,
            JiluZhuangtai zhuangtai,
            long page,
            long size
    );
    boolean shanchu(String yonghuId,String jiluId);
    boolean piliangShanchu(String yonghuId,List<String> jiluIdList);
}
