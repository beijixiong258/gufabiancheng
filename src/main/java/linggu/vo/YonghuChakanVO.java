package linggu.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YonghuChakanVO {
    private String id;
    private String zhanghao;
    private String dianhua;
    private String youxiang;
}
