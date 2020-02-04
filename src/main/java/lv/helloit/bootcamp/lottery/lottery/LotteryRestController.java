package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class LotteryRestController {
    private final LotteryService lotteryService;

    public LotteryRestController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/start-registration")
    public Lottery createLottery(LotteryRegistrationDto lotteryDto){
        return lotteryService.createLottery(lotteryDto);
    }

    @GetMapping("/lotteries")
    public ArrayList<Lottery> getAllLotteries() {
        return this.lotteryService.getAllLotteries();
    }
}
