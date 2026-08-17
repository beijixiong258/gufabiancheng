package linggu.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import linggu.enums.XiaoxiLeixing;
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
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;//消息ID
    @TableField("huihua_id")
    private String huihuaId;//所属会话ID
    @TableField("neirong")
    private String neirong;//消息的内容
    @TableField("type")
    private XiaoxiLeixing type;//消息类型：USER、ASSISTANT、SYSTEM


    @TableField("chansheng_shijian")
    private LocalDateTime chanshengShijian;//消息的产生时间
}
