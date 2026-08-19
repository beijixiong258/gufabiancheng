package linggu.vo;

import linggu.enums.Quanxian;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YonghuChakanVO {
    private String id;
    private String zhanghao;
    private String dianhua;
    private String youxiang;
    private Quanxian quanxian;
}
