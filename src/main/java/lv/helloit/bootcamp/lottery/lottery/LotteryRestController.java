package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public class LotteryRestController {
    private final LotteryService lotteryService;

    public LotteryRestController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/start-registration")
    public ResponseEntity<String> createLottery(
            @Valid @RequestBody LotteryRegistrationDto lotteryDto,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
//            String wrongField = bindingResult.getFieldErrors().get(0).getField();
            return new ResponseEntity<>("{\n" +
                    "\"status\":  \"Fail\",\n" +
                    "\"reason\": \"Please provide valid lottery properties. Title has to be provided and limit has to be numeric and larger than 1\"\n" +
                    "}", HttpStatus.CREATED);

        } else {
            Lottery lottery = lotteryService.createLottery(lotteryDto);
            return new ResponseEntity<>("{\n" +
                    "\"status\":  \"OK\",\n" +
                    "\"id\": " + lottery.getId() + "\n" +
                    "}", HttpStatus.CREATED);
        }
    }

    @GetMapping("/lotteries")
    public ArrayList<Lottery> getAllLotteries() {
        return this.lotteryService.getAllLotteries();
    }
}
