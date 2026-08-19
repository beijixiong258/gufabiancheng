package linggu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QianDuanController {
    @GetMapping({"/", "/login"})
    public String shouye() {
        return "forward:/index.html";
    }
}