package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class LotteryRestController {
    private final LotteryService lotteryService;

    public LotteryRestController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/start-registration")
    public ResponseEntity<String> createLottery(
            @Valid @RequestBody LotteryRegistrationDto lotteryDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String wrongField = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>("{\n" +
                    "\"status\":  \"Fail\",\n" +
                    "\"reason\": \""+ wrongField +"\"\n" +
                    "}", HttpStatus.BAD_REQUEST);

        } else {
            Lottery lottery = lotteryService.createLottery(lotteryDto);
            return new ResponseEntity<>("{\n" +
                    "\"status\":  \"OK\",\n" +
                    "\"id\": " + lottery.getId() + "\n" +
                    "}", HttpStatus.CREATED);
        }
    }

    @GetMapping("/status")
    public List<Lottery> getAllLotteries() {
        return this.lotteryService.getAllLotteries();
    }
}
