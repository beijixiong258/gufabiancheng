package linggu.controller;

import jakarta.validation.Valid;
import linggu.common.CommonException;
import linggu.common.Result;
import linggu.dto.XiaoxiXinjianDTO;
import linggu.entity.Xiaoxi;
import linggu.service.AIChatService;
import linggu.service.XiaoxiService;
import linggu.vo.DuihuaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/xiaoxi")
public class DuihuaController {
    private final XiaoxiService xiaoxiService;
    private final AIChatService aiChatService;

    @PostMapping("/fasong")
    public Result<DuihuaVO> fasongXiaoxi(@RequestAttribute String yonghuId,
                                         @RequestParam String huihuaId,
                                         @RequestParam("command") Integer mingling,
                                         @Valid @RequestBody(required = false) XiaoxiXinjianDTO xiaoxiXinjianDTO){
        DuihuaVO duihuaVO = switch (mingling) {
            case 0 -> {
                if (xiaoxiXinjianDTO==null) {
                    throw new CommonException(400,"消息内容不能为空。");
                }
                yield aiChatService.duihua(yonghuId,huihuaId,xiaoxiXinjianDTO.getNeirong());
            }
            case 1 -> aiChatService.jixuYilun(yonghuId,huihuaId);
            case 2 -> aiChatService.shengchengZhengwen(yonghuId,huihuaId);
            default -> throw new CommonException(400,"消息命令非法。");
        };
        return Result.success(duihuaVO);
    }

    @GetMapping("/chakanliebiao")
    public Result<List<Xiaoxi>> chakanXiaoxiLiebiao(@RequestAttribute String yonghuId,
                                                     @RequestParam String huihuaId){
        return Result.success(xiaoxiService.chakanLiebiao(yonghuId,huihuaId));
    }

    @GetMapping("/lianjie")
    public Result<Void> lianjie(){
        boolean success=aiChatService.lianjie();
        if (!success){
            return Result.fail(500,"AI连接失败。");
        }
        return Result.success();
    }
}
