package linggu.entity;

import linggu.enums.Quanxian;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Yonghu {
    private String id;//用户ID
    private String zhanghao;//账号
    private String mima;//密码
    private String dianhua;//手机号
    private String shenfenzheng;//大陆身份证
    private String youxiang;//邮箱
    private Quanxian quanxian;
}
