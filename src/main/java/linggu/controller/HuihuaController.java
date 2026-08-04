package linggu.controller;

import linggu.common.Result;
import linggu.entity.Huihua;
import linggu.service.HuihuaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/huihua")
public class HuihuaController {
    private final HuihuaService huihuaService;
    @PostMapping("/xinjian")
    public Result<String> xinjianHuihua(@RequestAttribute String yonghuId,@RequestParam String jiluId){
        return Result.success(huihuaService.xinjian(yonghuId,jiluId));
    }
    @GetMapping("/liebiao")
    public Result<List<Huihua>> chakanHuihuaLiebiao(@RequestAttribute String yonghuId,@RequestParam String jiluId){
        return Result.success(huihuaService.chakanLiebiao(yonghuId, jiluId));
    }
}
