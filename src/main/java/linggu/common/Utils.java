package linggu.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;

import java.util.UUID;

public class Utils {
    //创建ID
    public static String generateId(){
        String uuid= UUID.randomUUID().toString();
        return uuid.replace("-","");
    }
    //密码加密
    public static String mimaJiami(String mima){
        return BCrypt.hashpw(mima);
    }
    //加密检查，比对未加密的和已加密的字符串
    public static boolean jiamiJiancha(String yuanMima,String jiamiMima){
        if (StrUtil.isNotBlank(yuanMima)){
            return BCrypt.checkpw(yuanMima,jiamiMima);
        }
        return false;
    }
}
