package linggu.controller;

import linggu.common.Result;
import linggu.entity.Huihua;
import linggu.service.HuihuaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/huihua")
public class HuihuaController {
    private final HuihuaService huihuaService;
    @PostMapping("/new")
    public Result<String> xinjianHuihua(@RequestAttribute String yonghuId,@RequestParam String jiluId){
        return Result.success(huihuaService.xinjian(yonghuId,jiluId));
    }
    @GetMapping("/get")
    public Result<Huihua> chakanHuihua(@RequestAttribute String yonghuId,@RequestParam String huihuaId){
        return Result.success(huihuaService.chakan(yonghuId, huihuaId));
    }
    @GetMapping("/getlist")
    public Result<List<Huihua>> chakanHuihuaLiebiao(@RequestAttribute String yonghuId,@RequestParam String jiluId){
        return Result.success(huihuaService.chakanLiebiao(yonghuId, jiluId));
    }
    @DeleteMapping("/delete")
    public Result<Void> shanchuHuihua(@RequestAttribute String yonghuId, @RequestParam String huihuaId) {
        if (!huihuaService.shanchu(yonghuId, huihuaId)) {
            return Result.fail(500, "内部错误，会话删除失败。");
        }
        return Result.success();
    }
}
