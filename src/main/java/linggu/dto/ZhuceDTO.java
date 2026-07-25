package linggu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static linggu.common.Validation.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ZhuceDTO {
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
}
