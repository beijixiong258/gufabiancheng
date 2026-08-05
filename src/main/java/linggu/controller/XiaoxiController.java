package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.XiaoxiXinjianDTO;
import linggu.entity.Xiaoxi;
import linggu.service.XiaoxiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/xiaoxi")
public class XiaoxiController {
    private final XiaoxiService xiaoxiService;
    @PostMapping("/xinjian")
    public Result<Xiaoxi> xinjianXiaoxi(@RequestAttribute String yonghuId, @RequestParam String huihuaId, @Valid @RequestBody XiaoxiXinjianDTO xiaoxiXinjianDTO){
        return Result.success(xiaoxiService.xinjian(yonghuId,huihuaId, xiaoxiXinjianDTO.getNeirong(), 1));
    }
    @GetMapping("/chakanliebiao")
    public Result<List<Xiaoxi>> chakanXiaoxiLiebiao(@RequestAttribute String yonghuId, @RequestParam String huihuaId){
        return Result.success(xiaoxiService.chakanLiebiao(yonghuId, huihuaId));
    }
}
