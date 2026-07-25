package linggu.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import linggu.enums.Ticai;
import linggu.enums.Zhuangtai;
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
    private String id;//记录ID
    private String timu;//题目
    private Ticai ticai;//记录的文本题材
    private String biaoqian;//用于分组的标签
    private String zhengwen;//存储的正文，会不断更新
    private Zhuangtai zhuangtai;//记录状态，0为未完成，1为完成
    private String yonghuId;//所属用户的用户ID
    private DateTime chuangjianShijian;//创建时间
    private DateTime xiugaiShijian;//修改时间
}
