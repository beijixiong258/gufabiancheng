package linggu.vo;

import cn.hutool.core.date.DateTime;
import linggu.enums.Ticai;
import linggu.enums.Zhuangtai;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JiluLiebiaoVO {
    private String id;
    private String timu;
    private Ticai ticai;
    private String biaoqian;
    private Zhuangtai zhuangtai;
    private DateTime chuangjianShijian;
    private DateTime xiugaiShijian;
}
