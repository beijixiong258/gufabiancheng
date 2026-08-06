package linggu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import linggu.enums.Ticai;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JiluXiugaiDTO {
    @NotBlank(message = "题目不能为空。")
    @Size(max = 50, message = "题目不超过50位。")
    private String timu;
    @NotNull(message = "题材不能为空。")
    private Ticai ticai;
    @Size(max = 20, message = "标签不超过20位。")
    private String biaoqian;
    @Size(max = 2000, message = "正文长度不能超过2000个字符。")
    private String zhengwen;
}
