package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller()
public class LotteryController {

    @GetMapping("/public/")
    public String index() {
        return "index";
    }

}
