package linggu.ai;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AIChatResponse {
    private Integer zhuangtai;
    private String yindao;
    private String quanwen;
}
