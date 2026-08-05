package linggu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class XiaoxiXinjianDTO {
    @NotBlank(message = "消息内容不能为空。")
    private String neirong;
}
