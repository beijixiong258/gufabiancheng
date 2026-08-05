package linggu.common.ai;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AIChatResponse {//要求AI返回的结果必须是这种3字段的json，想办法严格限制AI的输出。注意我用的是小米mimo
    private Integer zhuangtai;//状态码，1为正文需要更新，0为引导
    private String yindao;//引导内容，状态非1则为空
    private String quanwen;
}
