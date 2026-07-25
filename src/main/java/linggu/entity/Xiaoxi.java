package linggu.entity;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Xiaoxi {
    private String id;//消息ID
    private String huihuaId;//所属会话ID
    private String neirong;//消息的内容
    private int laiyuan;//0是AI，1是人类用户
    private int xuhao;//消息在会话中的序号
    private DateTime chanshengShijian;//消息的产生时间
}
