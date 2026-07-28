package linggu.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import static linggu.common.Validation.DIANHUA;
import static linggu.common.Validation.YOUXIANG;

@Data
@Accessors(chain = true)
public class YonghuGengxinDTO {
    @Size(min = 4,max = 32,message = "账号应为4-32位")
    private String zhanghao;
    @Pattern(regexp = DIANHUA,message = "手机号非法")
    private String dianhua;
    @Pattern(regexp = YOUXIANG,message = "邮箱非法")
    private String youxiang;
}
