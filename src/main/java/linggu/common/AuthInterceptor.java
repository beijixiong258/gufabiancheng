package linggu.common;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import linggu.entity.Yonghu;
import linggu.enums.Quanxian;
import linggu.service.YonghuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private static final String PREFIX="Bearer ";
    private final LoginManager loginManager;
    private final YonghuService yonghuService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || (!authorization.regionMatches(true, 0, PREFIX, 0, PREFIX.length()))){
            throw new CommonException(401, "Token非法。");
        }
        String token=authorization.substring(PREFIX.length()).trim();
        if (token.isBlank()){
            throw new CommonException(401,"Token为空。");
        }
        String yonghuId= loginManager.get(token);
        if (StrUtil.isBlank(yonghuId)){
            throw new CommonException(401,"Token无效。");
        }
        Yonghu yonghu=yonghuService.getById(yonghuId);
        if (yonghu==null){
            loginManager.remove(token);
            throw new CommonException(401,"用户不存在。");
        }
        request.setAttribute("yonghuId",yonghuId);
        request.setAttribute("token",token);
        request.setAttribute("quanxian",yonghu.getQuanxian());
        Object pathAttribute=request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String path;
        if (pathAttribute==null){
            path="";
        }
        else {
            path=pathAttribute.toString();
        }
        boolean isAdmin=path.equals("/api/admin") || path.startsWith("/api/admin/");
        if (isAdmin && yonghu.getQuanxian()!= Quanxian.ADMIN){
            throw new CommonException(403,"没有管理员权限。");
        }
        return true;
    }
}
