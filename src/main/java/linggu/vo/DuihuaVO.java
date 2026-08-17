package linggu.vo;

import linggu.entity.Xiaoxi;
import linggu.enums.DuihuaZhuangtai;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DuihuaVO {
    private Xiaoxi renleiXiaoxi;//人类消息
    private Xiaoxi aiHuida;//AI回答
    private DuihuaZhuangtai zhuangtai;//本次AI处理结果
}
