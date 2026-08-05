package linggu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import linggu.enums.Quanxian;
import lombok.Data;
import lombok.experimental.Accessors;

import static linggu.common.Validation.DIANHUA;
import static linggu.common.Validation.SHENFENZHENG;
import static linggu.common.Validation.YOUXIANG;
@Data
@Accessors(chain = true)
public class YonghuXinjianDTO {
    @NotBlank(message = "请输入账号")
    @Size(min = 4,max = 32,message = "账号应为4-32位")
    private String zhanghao;
    @NotBlank(message = "请输入密码")
    @Size(min = 6,max = 32,message = "密码应为6-32位")
    private String mima;
    @Pattern(regexp = DIANHUA,message = "手机号非法")
    private String dianhua;
    @Pattern(regexp = SHENFENZHENG,message = "身份证号非法")
    private String shenfenzheng;
    @Pattern(regexp = YOUXIANG,message = "邮箱非法")
    private String youxiang;
    @NotNull(message = "请输入权限")
    private Quanxian quanxian;
}
