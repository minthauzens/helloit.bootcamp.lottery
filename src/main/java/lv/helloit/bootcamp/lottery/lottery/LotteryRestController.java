package lv.helloit.bootcamp.lottery.lottery;

import lombok.extern.slf4j.Slf4j;
import lv.helloit.bootcamp.lottery.participant.Participant;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

import static lv.helloit.bootcamp.lottery.utils.ResponseEntityBuilder.*;

@Slf4j
@RestController
public class LotteryRestController {
    private final LotteryService lotteryService;
    private final LotteryValidator lotteryValidator;
    private final ParticipantService participantService;

    public LotteryRestController(LotteryService lotteryService, LotteryValidator lotteryValidator, ParticipantService participantService) {
        this.lotteryService = lotteryService;
        this.lotteryValidator = lotteryValidator;
        this.participantService = participantService;
    }

    @PostMapping("/start-registration")
    public ResponseEntity<String> createLottery(
            @Valid @RequestBody LotteryRegistrationDto lotteryDto,
            BindingResult bindingResult) {
        log.info("Starting registration of LotteryRegistrationDto: " + lotteryDto);
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.info("Registration stopped: " + reason);
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = lotteryValidator.validateForRegistration(lotteryDto);
        if (response.hasErrors()) {
            log.info("Registration stopped: " + response.getMessage());
            return createResponseEntityFail(response.getMessage());
        }
        Lottery lottery = lotteryService.createLottery(lotteryDto);
        log.info("Lottery " + lottery.getTitle() + " created successfully");
        return createResponseEntityOkWithId(lottery.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ArrayList<LotteryWithParticipantCountDto> getAllWithParticipantCount() {
        log.info("returning all lottery stats");
        return this.lotteryService.getAllWithParticipantCount();
    }

    @PostMapping("/stop-registration")
    public ResponseEntity<String> stopRegistration(@Valid @RequestBody LotteryIdDto lotteryIdDto,
                                                   BindingResult bindingResult) {
        log.info("Trying to stop Lottery with id: " + lotteryIdDto.getId());
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.info("Failed: " + reason);
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = this.lotteryValidator.validateForStopRegistration(lotteryIdDto);
        if (response.hasErrors()) {
            log.info("Failed: " + response.getMessage());
            return createResponseEntityFail(response.getMessage());
        }

        this.lotteryService.stopRegistration(lotteryIdDto.getId());
        log.info("Lottery with id: " + lotteryIdDto.getId() + " registration stopped");
        return new ResponseEntity<>("{\"status\": \"OK\"}", HttpStatus.OK);
    }

    @PostMapping("/choose-winner")
    public ResponseEntity<String> chooseWinner(@Valid @RequestBody LotteryIdDto lotteryIdDto, BindingResult bindingResult) {
        log.info("Trying to choose winner for lottery with id: " + lotteryIdDto.getId());
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.info("Couldn't choose winner: " + reason);
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = this.lotteryValidator.validateForChooseWinner(lotteryIdDto);
        if (response.hasErrors()) {
            log.info("Couldn't choose winner: " + response.getMessage());
            return createResponseEntityFail(response.getMessage());
        }
        if (!(this.participantService.existsByLotteryId(lotteryIdDto.getId()))) {
            log.info("This lottery has no participants!");
            return createResponseEntityFail("This lottery has no participants!");
        }
        Participant participant = this.participantService.chooseLotteryWinner(lotteryIdDto.getId());
        this.lotteryService.setLotteryCompleted(lotteryIdDto.getId());
        String winnerCode = participant.getCode();
        log.info("Lottery (id: " + lotteryIdDto.getId() + ") winner is code: " + winnerCode);
        return createResponseEntityOkWithWinnerCode(winnerCode);
    }

}
