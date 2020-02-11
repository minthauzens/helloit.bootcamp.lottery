package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.ValidatorResponse;
import lv.helloit.bootcamp.lottery.lottery.LotteryIdDto;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import lv.helloit.bootcamp.lottery.lottery.LotteryValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static lv.helloit.bootcamp.lottery.ResponseEntityFactory.*;

@RestController
public class ParticipantRestController {
    private final ParticipantService participantService;
    private final ParticipantValidator participantValidator;
    private final LotteryValidator lotteryValidator;
    private final LotteryService lotteryService;

    public ParticipantRestController(ParticipantService participantService,
                                     ParticipantValidator participantValidator,
                                     LotteryValidator lotteryValidator,
                                     LotteryService lotteryService) {
        this.participantService = participantService;
        this.participantValidator = participantValidator;
        this.lotteryValidator = lotteryValidator;
        this.lotteryService = lotteryService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerParticipant(@Valid @RequestBody ParticipantRegisterDto participantDto,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = participantValidator.validate(participantDto);
        if (response.hasErrors()) {
            return createResponseEntityFail(response.getMessage());
        }
        Participant participant = participantService.createParticipant(participantDto);
        return createResponseEntityOkWithId(participant.getId());

    }

    @PostMapping("/choose-winner")
    public ResponseEntity<String> chooseWinner(@Valid @RequestBody LotteryIdDto lotteryIdDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = this.lotteryValidator.validateForChooseWinner(lotteryIdDto);
        if (response.hasErrors()) {
            return createResponseEntityFail(response.getMessage());
        }

        Participant participant = this.participantService.chooseLotteryWinner(lotteryIdDto.getId());
        String winnerCode = participant.getCode();
        return createResponseEntityOkWithWinnerCode(winnerCode);
    }
}
