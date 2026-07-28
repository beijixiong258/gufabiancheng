package linggu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YonghuDengluDTO {
    @NotBlank(message = "请输入账号")
    @Size(min = 4, max = 32, message = "账号应为4-32位")
    private String zhanghao;

    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 32, message = "密码应为6-32位")
    private String mima;
}
