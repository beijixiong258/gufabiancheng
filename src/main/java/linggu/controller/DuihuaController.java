package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.Result;
import linggu.dto.DuihuaDTO;
import linggu.entity.Xiaoxi;
import linggu.service.AIChatService;
import linggu.service.XiaoxiService;
import linggu.vo.DuihuaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/duihua")
public class DuihuaController {
    private final XiaoxiService xiaoxiService;
    private final AIChatService aiChatService;

    @PostMapping("/chat")
    public Result<DuihuaVO> duihua(@RequestAttribute String yonghuId, @Valid @RequestBody DuihuaDTO duihuaDTO){
        return Result.success(aiChatService.duihua(yonghuId,duihuaDTO));
    }
    @GetMapping("/getlist")
    public Result<List<Xiaoxi>> chakanXiaoxiLiebiao(@RequestAttribute String yonghuId, @RequestParam String huihuaId){
        return Result.success(xiaoxiService.chakanLiebiao(yonghuId,huihuaId));
    }
    @GetMapping("/test")
    public Result<Void> lianjie(){
        boolean success=aiChatService.lianjie();
        if (!success){
            return Result.fail(500,"AI连接失败。");
        }
        return Result.success();
    }
}
