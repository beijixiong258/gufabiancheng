package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.JiluXinjianDTO;
import linggu.entity.Jilu;
import linggu.service.JiluService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jilu")
@RequiredArgsConstructor
public class JiluController {
    private final JiluService jiluService;
    @PostMapping("/xinjian")
    public Result<Jilu> xinjianJilu(@RequestAttribute String yonghuId,@Valid @RequestBody JiluXinjianDTO jiluXinjianDTO){
        return Result.success(jiluService.xinjian(yonghuId,jiluXinjianDTO));
    }
}
