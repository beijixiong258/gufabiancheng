package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuXiugaiDTO;
import linggu.dto.YonghuXinjianDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.entity.Yonghu;
import linggu.vo.GuanliyuanChakanVO;
import linggu.vo.YonghuChakanVO;

import java.util.List;

public interface YonghuService extends IService<Yonghu> {
    boolean zhuce(YonghuZhuceDTO yonghuZhuceDTO);//新用户注册，默认权限为普通用户
    String denglu(YonghuDengluDTO yonghuDengluDTO);//用户登录，返回token
    String shuaxin(String token);//刷新用户的token
    boolean tuichu(String token);//退出登录，删除token
    YonghuChakanVO chakan(String yonghuId);//查看个人主页，不返回身份证和密码
    boolean xiugai(String yonghuId,YonghuXiugaiDTO yonghuXiugaiDTO);//修改指定字段的个人信息
    boolean xiugaiMima(String yonghuId,String mima1,String mima2);//用户修改密码
    boolean xinjian(YonghuXinjianDTO yonghuXinjianDTO);//管理员新建用户

    GuanliyuanChakanVO chakanYonghu(String yonghuId);//查看单个用户，只用于管理员查人
    List<GuanliyuanChakanVO> chakanLiebiao();//管理员查看用户列表
    boolean xiugai(Yonghu yonghu);//管理员全量修改用户信息
    boolean shanchu(String yonghuId);//管理员删除用户
}
