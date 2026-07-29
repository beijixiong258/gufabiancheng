package linggu.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
@Component
@RequiredArgsConstructor
public class LoginManager {//用redis管理key
    private static final String PREFIX="login:";
    private static final Duration LOGIN_TIME=Duration.ofDays(4);
    private static final Duration REFRESH_TIME=Duration.ofHours(5);
    private final StringRedisTemplate stringRedisTemplate;
    public String create(String yonghuId){
        String token=Utils.generateId();
        stringRedisTemplate.opsForValue().set(PREFIX+token,yonghuId,LOGIN_TIME);
        return token;
    }
    public String get(String token){
        String key=(PREFIX+token);
        return stringRedisTemplate.opsForValue().get(key);
    }
    public void remove(String token){
        String key=(PREFIX+token);
        stringRedisTemplate.delete(key);
    }
    public String refresh(String token){
        String key=PREFIX+token;
        String yonghuId=this.get(token);
        if (yonghuId==null){
            throw new CommonException(401,"Token无效。");
        }
        Long second=stringRedisTemplate.getExpire(key);
        if (second==null || second<=0){
            throw new CommonException(401,"Token过期。");
        }
        if (second>REFRESH_TIME.toSeconds()){
            return token;
        }
        else {
            this.remove(token);
            return this.create(yonghuId);
        }
    }
}
