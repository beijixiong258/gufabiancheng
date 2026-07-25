package linggu.controller;

import linggu.service.YonghuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class YonghuController {
    private final YonghuService yonghuService;
}
