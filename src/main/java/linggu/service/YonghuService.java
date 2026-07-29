package linggu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuGengxinDTO;
import linggu.dto.YonghuXinzengDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.entity.Yonghu;
import linggu.vo.GuanliyuanChaxunVO;
import linggu.vo.YonghuChakanVO;

import java.util.List;

public interface YonghuService extends IService<Yonghu> {
    boolean zhuce(YonghuZhuceDTO yonghuZhuceDTO);//新用户注册，默认权限为普通用户
    String denglu(YonghuDengluDTO yonghuDengluDTO);//用户登录，返回token
    String shuaxin(String token);//刷新用户的token
    boolean tuichu(String token);//退出登录，删除token
    YonghuChakanVO chakan(String yonghuId);//查看个人主页，不返回身份证和密码
    boolean gengxin(String id,YonghuGengxinDTO yonghuGengxinDTO);//更新指定字段的个人信息
    boolean xiugaiMima(String id,String mima1,String mima2);//用户修改密码
    boolean xinzeng(YonghuXinzengDTO yonghuXinzengDTO);//管理员新增用户

    GuanliyuanChaxunVO chaxun(String yonghuId);//查询单个用户，只用于管理员查人
    List<GuanliyuanChaxunVO> huoquLiebiao();//管理员获取用户列表
    boolean xiugai(Yonghu yonghu);//管理员全量修改用户信息
    boolean shanchu(String yonghuId);//管理员删除用户
}
