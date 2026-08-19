package linggu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("huihua")
public class Huihua {
    @TableId("id")
    private String id;//会话ID
    @TableField("jilu_id")
    private String jiluId;//所属记录的ID
    @TableField("mingcheng")
    private String mingcheng;//会话名称
    @TableField("chuangjian_shijian")
    private LocalDateTime chuangjianShijian;//创建时间

    public Huihua(String id, String jiluId) {
        this.id = id;
        this.jiluId = jiluId;
        this.mingcheng = "新会话";
    }
}
