package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.CommonException;
import linggu.common.Result;
import linggu.dto.YonghuDengluDTO;
import linggu.dto.YonghuGengxinDTO;
import linggu.dto.YonghuXinzengDTO;
import linggu.dto.YonghuZhuceDTO;
import linggu.entity.Yonghu;
import linggu.service.YonghuService;
import linggu.vo.GuanliyuanChaxunVO;
import linggu.vo.YonghuChakanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class YonghuController {
    private final YonghuService yonghuService;
    @PostMapping("/zhuce")
    public Result<Void> zhuce(@Valid @RequestBody YonghuZhuceDTO yonghuZhuceDTO){
        if (!yonghuService.zhuce(yonghuZhuceDTO)){
            throw new CommonException(500,"内部错误，注册失败。");
        }
        return Result.success();
    }
    @PostMapping("/denglu")
    public Result<String> denglu(@Valid @RequestBody YonghuDengluDTO yonghuDengluDTO){
        String token= yonghuService.denglu(yonghuDengluDTO);
        return Result.success(token);
    }
    @PostMapping("/shuaxin")
    public Result<String> shuaxin(@RequestAttribute String token){
        String newToken=yonghuService.shuaxin(token);
        return Result.success(newToken);
    }
    @PostMapping("/tuichu")
    public Result<Void> tuichu(@RequestAttribute String token){
        yonghuService.tuichu(token);
        return Result.success();
    }
    @GetMapping("/chakan")
    public Result<YonghuChakanVO> chakan(@RequestAttribute String yonghuId){
        return Result.success(yonghuService.chakan(yonghuId));
    }
    @PutMapping("/mima")
    public Result<Void> xiugaiMima(@RequestAttribute String yonghuId, @RequestParam String mima1, @RequestParam String mima2){
        if (mima2.isBlank() || mima2.length()<6 ||mima2.length()>32){
            throw new CommonException(400,"新密码非法。");
        }
        if (!yonghuService.xiugaiMima(yonghuId,mima1,mima2)){
            throw new CommonException(500,"内部错误，密码修改失败。");
        }
        return Result.success();
    }
    @PutMapping("/gengxin")
    public Result<Void> gengxin(@RequestAttribute String yonghuId,@Valid @RequestBody YonghuGengxinDTO yonghuGengxinDTO){
        if(yonghuService.gengxin(yonghuId,yonghuGengxinDTO)){
            return Result.success();
        }
        else {
            return Result.fail(500,"内部错误，信息更新失败。");
        }
    }
    @PostMapping("/admin/xinzeng")
    public Result<Void> xinzeng(@Valid @RequestBody YonghuXinzengDTO yonghuXinzengDTO){
        if (yonghuService.xinzeng(yonghuXinzengDTO)){
            return Result.success();
        }
        else {
            return Result.fail(500,"内部错误，用户创建失败");
        }
    }
    @GetMapping("/admin/chaxun")
    public Result<GuanliyuanChaxunVO> chaxun(@RequestParam String yonghuId){
        return Result.success(yonghuService.chaxun(yonghuId));
    }
    @GetMapping("/admin")
    public Result<List<GuanliyuanChaxunVO>> huoquLiebiao(){
        return Result.success(yonghuService.huoquLiebiao());
    }
    @PutMapping("/admin/{id}")
    public Result<Void> xiugai(@PathVariable("id") String id,@RequestBody Yonghu yonghu){
        yonghu.setId(id);
        if (!yonghuService.xiugai(yonghu)){
            return Result.fail(500,"内部错误，用户修改失败。");
        }
        return Result.success();
    }
    @DeleteMapping("/admin/{id}")
    public Result<Void> shanchu(@PathVariable("id") String yonghuId){
        if (!yonghuService.shanchu(yonghuId)){
            return Result.fail(500,"内部错误，用户删除失败。");
        }
        return Result.success();
    }

}
