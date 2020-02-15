package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.lottery.LotteryIdDto;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import lv.helloit.bootcamp.lottery.lottery.LotteryValidator;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static lv.helloit.bootcamp.lottery.utils.ResponseEntityBuilder.*;

@RestController
public class ParticipantRestController {
    private static final Logger LOGGER = LogManager.getLogger(ParticipantRestController.class);

    private final ParticipantService participantService;
    private final ParticipantValidator participantValidator;
    private final LotteryValidator lotteryValidator;
    private final LotteryService lotteryService;

    public ParticipantRestController(ParticipantService participantService,
                                     ParticipantValidator participantValidator,
                                     LotteryValidator lotteryValidator, LotteryService lotteryService) {
        this.participantService = participantService;
        this.participantValidator = participantValidator;
        this.lotteryValidator = lotteryValidator;
        this.lotteryService = lotteryService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerParticipant(@Valid @RequestBody ParticipantRegisterDto participantDto,
                                                      BindingResult bindingResult) {
        LOGGER.info("Trying to register participant " + participantDto);
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            LOGGER.info("Registration stopped: " + reason);
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = participantValidator.validate(participantDto);
        if (response.hasErrors()) {
            LOGGER.info("Registration stopped: " + response.getMessage());
            return createResponseEntityFail(response.getMessage());
        }
        Long id = participantService.createParticipant(participantDto);
        LOGGER.info("Participant created");
        return createResponseEntityOkWithId(id, HttpStatus.CREATED);
    }

    @PostMapping("/choose-winner")
    public ResponseEntity<String> chooseWinner(@Valid @RequestBody LotteryIdDto lotteryIdDto, BindingResult bindingResult) {
        LOGGER.info("Trying to choose winner for lottery with id: " + lotteryIdDto.getId());
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            LOGGER.info("Couldn't choose winner: " + reason);
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = this.lotteryValidator.validateForChooseWinner(lotteryIdDto);
        if (response.hasErrors()) {
            LOGGER.info("Couldn't choose winner: " + response.getMessage());
            return createResponseEntityFail(response.getMessage());
        }
        if (!(this.participantService.existsByLotteryId(lotteryIdDto.getId()))) {
            LOGGER.info("This lottery has no participants!");
            return createResponseEntityFail("This lottery has no participants!");
        }
        Participant participant = this.participantService.chooseLotteryWinner(lotteryIdDto.getId());
        this.lotteryService.setLotteryCompleted(lotteryIdDto.getId());
        String winnerCode = participant.getCode();
        LOGGER.info("Lottery (id: " + lotteryIdDto.getId() + ") winner is code: " + winnerCode);
        return createResponseEntityOkWithWinnerCode(winnerCode);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getParticipantStatus(@Valid ParticipantStatusDto participantStatusDto,
                                                       BindingResult bindingResult) {
        LOGGER.info("Trying to get status of " + participantStatusDto);
        if (bindingResult.hasErrors()) {
            LOGGER.info("Failed to get participant status: because not valid data provided");
            return new ResponseEntity<>("\"status\": \"ERROR\"", HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Returning info");
        return this.participantService.getParticipantStatus(participantStatusDto);
    }
}
