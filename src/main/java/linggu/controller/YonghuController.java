package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.CommonException;
import linggu.common.Result;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuXiugaiDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.service.YonghuService;
import linggu.vo.YonghuChakanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/yonghu")
public class YonghuController {
    private final YonghuService yonghuService;
    @PostMapping("/register")
    public Result<Void> zhuce(@Valid @RequestBody YonghuZhuceDTO yonghuZhuceDTO){
        if (!yonghuService.zhuce(yonghuZhuceDTO)){
            throw new CommonException(500,"内部错误，注册失败。");
        }
        return Result.success();
    }
    @PostMapping("/login")
    public Result<String> denglu(@Valid @RequestBody YonghuDengluDTO yonghuDengluDTO){
        String token= yonghuService.denglu(yonghuDengluDTO);
        return Result.success(token);
    }
    @PostMapping("/refresh")
    public Result<String> shuaxin(@RequestAttribute String token){
        String newToken=yonghuService.shuaxin(token);
        return Result.success(newToken);
    }
    @PostMapping("/logout")
    public Result<Void> tuichu(@RequestAttribute String token){
        yonghuService.tuichu(token);
        return Result.success();
    }
    @GetMapping("/me")
    public Result<YonghuChakanVO> chakan(@RequestAttribute String yonghuId){
        return Result.success(yonghuService.chakan(yonghuId));
    }
    @PutMapping("/password")
    public Result<Void> xiugaiMima(@RequestAttribute String yonghuId, @RequestParam String mima1, @RequestParam String mima2){
        if (mima2.isBlank() || mima2.length()<6 ||mima2.length()>32){
            throw new CommonException(400,"新密码非法。");
        }
        if (!yonghuService.xiugaiMima(yonghuId,mima1,mima2)){
            throw new CommonException(500,"内部错误，密码修改失败。");
        }
        return Result.success();
    }
    @PutMapping("/update")
    public Result<Void> xiugai(@RequestAttribute String yonghuId,@Valid @RequestBody YonghuXiugaiDTO yonghuXiugaiDTO){
        if(yonghuService.xiugai(yonghuId,yonghuXiugaiDTO)){
            return Result.success();
        }
        else {
            return Result.fail(500,"内部错误，信息修改失败。");
        }
    }
}
