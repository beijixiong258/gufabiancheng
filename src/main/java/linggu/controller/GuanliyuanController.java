package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.GuanliyuanXiugaiDTO;
import linggu.dto.YonghuXinjianDTO;
import linggu.service.YonghuService;
import linggu.vo.GuanliyuanChakanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class GuanliyuanController {
    private final YonghuService yonghuService;
    @PostMapping("/add")
    public Result<Void> xinjianYonghu(@Valid @RequestBody YonghuXinjianDTO yonghuXinjianDTO){
        if (yonghuService.xinjian(yonghuXinjianDTO)){
            return Result.success();
        }
        else {
            return Result.fail(500,"内部错误，用户新建失败");
        }
    }
    @GetMapping("/get")
    public Result<GuanliyuanChakanVO> chakanYonghu(@RequestParam String yonghuId){
        return Result.success(yonghuService.chakanYonghu(yonghuId));
    }
    @GetMapping("/getlist")
    public Result<List<GuanliyuanChakanVO>> chakanYonghuLiebiao(){
        return Result.success(yonghuService.chakanLiebiao());
    }
    @PutMapping("/modify")
    public Result<Void> xiugaiYonghu(@Valid @RequestBody GuanliyuanXiugaiDTO guanliyuanXiugaiDTO){
        if (!yonghuService.xiugai(guanliyuanXiugaiDTO)){
            return Result.fail(500,"内部错误，用户修改失败。");
        }
        return Result.success();
    }
    @DeleteMapping("/delete")
    public Result<Void> shanchuYonghu(@RequestParam("id") String yonghuId){
        if (!yonghuService.shanchu(yonghuId)){
            return Result.fail(500,"内部错误，用户删除失败。");
        }
        return Result.success();
    }
}
