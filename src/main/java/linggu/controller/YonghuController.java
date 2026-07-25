package linggu.controller;

import linggu.common.Result;
import linggu.service.YonghuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class YonghuController {
    private final YonghuService yonghuService;
    @PostMapping("/zhuce")
    public Result zhuce(){//咕咕咕
        return Result.success();
    }
}
