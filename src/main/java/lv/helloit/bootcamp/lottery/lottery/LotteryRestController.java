package lv.helloit.bootcamp.lottery.lottery;

import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static lv.helloit.bootcamp.lottery.utils.ResponseEntityBuilder.*;

@RestController
public class LotteryRestController {
    private final LotteryService lotteryService;
    private final LotteryValidator lotteryValidator;

    public LotteryRestController(LotteryService lotteryService, LotteryValidator lotteryValidator) {
        this.lotteryService = lotteryService;
        this.lotteryValidator = lotteryValidator;
    }

    @PostMapping("/start-registration")
    public ResponseEntity<String> createLottery(
            @Valid @RequestBody LotteryRegistrationDto lotteryDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return createResponseEntityFail(reason);
        }
        Lottery lottery = lotteryService.createLottery(lotteryDto);
        return createResponseEntityOkWithId(lottery.getId());
    }

    @GetMapping("/stats")
    public List<LotteryWithParticipantCountDto> getAllWithParticipantCount() {
        return this.lotteryService.getAllWithParticipantCount();
    }

    @PostMapping("/stop-registration")
    public ResponseEntity<String> stopRegistration(@Valid @RequestBody LotteryIdDto lotteryIdDto,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = this.lotteryValidator.validateForStopRegistration(lotteryIdDto);
        if (response.hasErrors()) {
            return createResponseEntityFail(response.getMessage());
        }
        this.lotteryService.stopRegistration(lotteryIdDto.getId());
        return createResponseEntityOk();
    }
}
