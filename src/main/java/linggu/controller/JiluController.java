package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.JiluXinjianDTO;
import linggu.dto.JiluXiugaiDTO;
import linggu.entity.Jilu;
import linggu.service.JiluService;
import linggu.vo.JiluLiebiaoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jilu")
public class JiluController {
    private final JiluService jiluService;
    @PostMapping("/xinjian")
    public Result<Jilu> xinjianJilu(@RequestAttribute String yonghuId,@Valid @RequestBody JiluXinjianDTO jiluXinjianDTO){
        return Result.success(jiluService.xinjian(yonghuId,jiluXinjianDTO));
    }
    @PutMapping("/xiugai")
    public Result<Void> xiugaiJilu(@RequestAttribute String yonghuId,@RequestParam("id") String jiluId, @Valid @RequestBody JiluXiugaiDTO jiluXiugaiDTO){
        boolean success=jiluService.xiugai(yonghuId,jiluId,jiluXiugaiDTO);
        if (!success){
            return Result.fail(500,"内部错误，记录修改失败。");
        }
        return Result.success();
    }
    @PutMapping("/wancheng")
    public Result<Void> wanchengJilu(@RequestAttribute String yonghuId, @RequestParam("id") String jiluId) {
        boolean success = jiluService.wancheng(yonghuId, jiluId);
        if (!success) {
            return Result.fail(500, "内部错误，状态修改失败。");
        }
        return Result.success();
    }
    @GetMapping("/chakan")
    public Result<Jilu> chakanJilu(@RequestAttribute String yonghuId,@RequestParam("id") String jiluId){
        return Result.success(jiluService.chakan(yonghuId, jiluId));
    }
    @GetMapping("/chakanliebiao")
    public Result<List<JiluLiebiaoVO>> chakanLiebiao(@RequestAttribute String yonghuId){
        return Result.success(jiluService.chakanLiebiao(yonghuId));
    }
    @DeleteMapping("/shanchu")
    public Result<Void> shanchuJilu(@RequestAttribute String yonghuId,@RequestParam("id") String jiluId){
        boolean success= jiluService.shanchu(yonghuId, jiluId);
        if (!success){
            return Result.fail(500,"内部错误，记录删除失败");
        }
        return Result.success();
    }
    @DeleteMapping("/piliangshanchu")
    public Result<Void> piliangShanchu(@RequestAttribute String yonghuId,@RequestBody List<String> jiluIdList){
        boolean success= jiluService.piliangShanchu(yonghuId, jiluIdList);
        if (!success){
            return Result.fail(500,"内部错误，记录批量删除失败。");
        }
        return Result.success();
    }
}
