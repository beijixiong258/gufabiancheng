package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.DengluDTO;
import linggu.dto.ZhuceDTO;
import linggu.service.YonghuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class YonghuController {
    private final YonghuService yonghuService;
    @PostMapping("/zhuce")
    public Result<Void> zhuce(@Valid @RequestBody ZhuceDTO zhuceDTO){
        if (!yonghuService.zhuce(zhuceDTO)){
            return Result.fail(500,"内部错误，注册失败。");
        }
        return Result.success();
    }
    @PostMapping("/denglu")
    public Result<String> denglu(@Valid @RequestBody DengluDTO dengluDTO){
        String token= yonghuService.denglu(dengluDTO);
        return Result.success(token);
    }
}
