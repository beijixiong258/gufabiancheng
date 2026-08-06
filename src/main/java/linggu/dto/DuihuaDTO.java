package linggu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DuihuaDTO {
    @NotBlank(message = "会话ID不能为空。")
    private String huihuaId;

    @NotNull(message = "消息命令不能为空。")
    @Min(value = 0, message = "消息命令非法。")
    @Max(value = 2, message = "消息命令非法。")
    private Integer command;
    private String neirong;
    @NotBlank(message = "当前正文不能为空。")
    @Size(max = 2000, message = "当前正文长度不能超过2000个字符。")
    private String zhengwen;
}
