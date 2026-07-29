package linggu.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("xiaoxi")
public class Xiaoxi {
    @TableId("id")
    private String id;//消息ID
    @TableField("huihua_id")
    private String huihuaId;//所属会话ID
    @TableField("neirong")
    private String neirong;//消息的内容
    @TableField("laiyuan")
    private int laiyuan;//0是AI，1是人类用户
    @TableField("xuhao")
    private int xuhao;//消息在会话中的序号
    @TableField("chansheng_shijian")
    private DateTime chanshengShijian;//消息的产生时间
}
