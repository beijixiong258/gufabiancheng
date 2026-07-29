package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.CommonException;
import linggu.common.Result;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.service.YonghuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class YonghuController {
    private final YonghuService yonghuService;
    @PostMapping("/zhuce")
    public Result<Void> zhuce(@Valid @RequestBody YonghuZhuceDTO yonghuZhuceDTO){
        if (!yonghuService.zhuce(yonghuZhuceDTO)){
            throw new CommonException(500,"内部错误，注册失败。");
        }
        return Result.success();
    }
    @PostMapping("/denglu")
    public Result<String> denglu(@Valid @RequestBody YonghuDengluDTO yonghuDengluDTO){
        String token= yonghuService.denglu(yonghuDengluDTO);
        return Result.success(token);
    }
    @PostMapping("/shuaxin")
    public Result<String> shuaxin(@RequestAttribute("token") String token){
        String newToken=yonghuService.shuaxin(token);
        return Result.success(newToken);
    }
    @PutMapping("/mima")
    public Result<Void> xiugaiMima(@RequestAttribute("yonghuId") String yonghuId, @RequestParam String mima1, @RequestParam String mima2){
        if (mima2.isBlank() || mima2.length()<6 ||mima2.length()>32){
            throw new CommonException(400,"新密码非法。");
        }
        if (!yonghuService.xiugaiMima(yonghuId,mima1,mima2)){
            throw new CommonException(500,"内部错误，密码修改失败。");
        }
        return Result.success();
    }
}
