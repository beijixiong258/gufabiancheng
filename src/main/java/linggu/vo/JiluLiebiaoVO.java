package linggu.vo;

import java.time.LocalDateTime;
import linggu.enums.Ticai;
import linggu.enums.JiluZhuangtai;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JiluLiebiaoVO {
    private String id;
    private String timu;
    private Ticai ticai;
    private String biaoqian;
    private JiluZhuangtai jiluZhuangtai;
    private LocalDateTime chuangjianShijian;
    private LocalDateTime xiugaiShijian;
}
