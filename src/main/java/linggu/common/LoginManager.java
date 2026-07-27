package linggu.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class LoginManager {//这玩意本质就是本科时候教的threadlocal模块，但是AI让我这么写，不用threadlocal，先顺从了。
    private final Map<String, String> map=new ConcurrentHashMap<>();
    //创建令牌
    public String create(String yonghuId){
        String token=Utils.generateId();
        map.put(token,yonghuId);
        return token;
    }
    //通过令牌查找用户ID
    public String get(String token){
        return map.get(token);
    }
    //移除令牌
    public void remove(String token){
        map.remove(token);
    }
    //刷新令牌
    public String refresh(String token){
        String yonghuId=this.get(token);
        if (yonghuId==null){
            throw new CommonException(401,"token无效。");
        }
        remove(token);
        return this.create(yonghuId);
    }
}
