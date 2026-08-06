package linggu.common.ai;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AIChatResponse {//要求AI返回的结果必须是这种3字段的json，想办法严格限制AI的输出。注意我用的是小米mimo
    private Integer zhuangtai;//状态码：0结束并生成正文，1继续追问，2等待用户选择
    private String yindao;//继续追问或等待选择时的AI回复
    private String quanwen;//结束时生成的完整正文
}
