package linggu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import linggu.enums.Quanxian;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("yonghu")
public class Yonghu {
    @TableId("id")
    private String id;//用户ID
    @TableField("zhanghao")
    private String zhanghao;//账号
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableField("mima")
    private String mima;//密码
    @TableField("dianhua")
    private String dianhua;//手机号
    @TableField("shenfenzheng")
    private String shenfenzheng;//大陆身份证
    @TableField("youxiang")
    private String youxiang;//邮箱
    @TableField("quanxian")
    private Quanxian quanxian;//用户身份，0是普通用户，1是管理员
}
