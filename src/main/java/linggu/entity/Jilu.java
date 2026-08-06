package linggu.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import linggu.enums.Ticai;
import linggu.enums.JiluZhuangtai;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("jilu")
public class Jilu {
    @TableId("id")
    private String id;//记录ID
    @TableField("timu")
    private String timu;//题目
    @TableField("ticai")
    private Ticai ticai;//记录的文本题材
    @TableField("biaoqian")
    private String biaoqian;//用于分组的标签
    @TableField("zhengwen")
    private String zhengwen;//存储的正文，会不断更新
    @TableField("zhuangtai")
    private JiluZhuangtai jiluZhuangtai;//记录状态，0为未完成，1为完成
    @TableField("yonghu_id")
    private String yonghuId;//所属用户的用户ID
    @TableField(value = "chuangjian_shijian", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private DateTime chuangjianShijian;//创建时间
    @TableField(value = "xiugai_shijian", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private DateTime xiugaiShijian;//修改时间
}
