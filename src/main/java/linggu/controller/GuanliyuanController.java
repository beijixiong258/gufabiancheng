package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.YonghuXinzengDTO;
import linggu.entity.Yonghu;
import linggu.service.YonghuService;
import linggu.vo.GuanliyuanChaxunVO;
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
@RequestMapping("/admin")
public class GuanliyuanController {
    private final YonghuService yonghuService;
    @PostMapping("/add")
    public Result<Void> tianjiaYonghu(@Valid @RequestBody YonghuXinzengDTO yonghuXinzengDTO){
        if (yonghuService.tianjia(yonghuXinzengDTO)){
            return Result.success();
        }
        else {
            return Result.fail(500,"内部错误，用户创建失败");
        }
    }
    @GetMapping("/get")
    public Result<GuanliyuanChaxunVO> chaxunYonghu(@RequestParam String yonghuId){
        return Result.success(yonghuService.chaxun(yonghuId));
    }
    @GetMapping("/getlist")
    public Result<List<GuanliyuanChaxunVO>> yonghuLiebiao(){
        return Result.success(yonghuService.huoquLiebiao());
    }
    @PutMapping("/modify")
    public Result<Void> xiugaiYonghu(@RequestBody Yonghu yonghu){
        if (!yonghuService.xiugai(yonghu)){
            return Result.fail(500,"内部错误，用户修改失败。");
        }
        return Result.success();
    }
    @DeleteMapping("/delete")
    public Result<Void> shanchuYonghu(@RequestParam("id") String id){
        if (!yonghuService.shanchu(id)){
            return Result.fail(500,"内部错误，用户删除失败。");
        }
        return Result.success();
    }
}
