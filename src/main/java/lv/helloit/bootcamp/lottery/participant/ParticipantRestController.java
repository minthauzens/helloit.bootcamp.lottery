package lv.helloit.bootcamp.lottery.participant;

import lombok.extern.slf4j.Slf4j;
import lv.helloit.bootcamp.lottery.utils.Response;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static lv.helloit.bootcamp.lottery.utils.ResponseEntityBuilder.createResponseEntityFail;
import static lv.helloit.bootcamp.lottery.utils.ResponseEntityBuilder.createResponseEntityOkWithId;

@Slf4j
@RestController
public class ParticipantRestController {
    private final ParticipantService participantService;
    private final ParticipantValidator participantValidator;


    public ParticipantRestController(ParticipantService participantService,
                                     ParticipantValidator participantValidator) {
        this.participantService = participantService;
        this.participantValidator = participantValidator;

    }

    @PostMapping("/register")
    public ResponseEntity<String> registerParticipant(@Valid @RequestBody ParticipantRegisterDto participantDto,
                                                      BindingResult bindingResult) {
        log.info("Trying to register participant " + participantDto);
        if (bindingResult.hasErrors()) {
            String reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.info("Registration stopped: " + reason);
            return createResponseEntityFail(reason);
        }
        ValidatorResponse response = participantValidator.validate(participantDto);
        if (response.hasErrors()) {
            log.info("Registration stopped: " + response.getMessage());
            return createResponseEntityFail(response.getMessage());
        }
        Long id = participantService.createParticipant(participantDto);
        log.info("Participant created");
        return createResponseEntityOkWithId(id, HttpStatus.CREATED);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getParticipantStatus(@Valid ParticipantStatusDto participantStatusDto,
                                                       BindingResult bindingResult) {
        log.info("Trying to get status of " + participantStatusDto);
        if (bindingResult.hasErrors()) {
            log.info("Failed to get participant status: because not valid data provided");
            return new ResponseEntity<>("\"status\": \"ERROR\"", HttpStatus.BAD_REQUEST);
        }
        log.info("Returning info");
        Response response = this.participantService.getParticipantStatus(participantStatusDto);
        if (response.hasErrors()) {
            return new ResponseEntity<>("\"status\": \"ERROR\"", response.getHttpStatus());
        }
        return new ResponseEntity<>("\"status\": \"" + response.getMessage() + "\"", response.getHttpStatus());
    }
}
