package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.lottery.Lottery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ParticipantRestController {
    private final ParticipantService participantService;
    private final ParticipantValidator participantValidator;

    public ParticipantRestController(ParticipantService participantService, ParticipantValidator participantValidator) {
        this.participantService = participantService;
        this.participantValidator = participantValidator;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerParticipant(@Valid @RequestBody ParticipantRegisterDto participantDto,
                                                      BindingResult bindingResult) {
        String status = "OK";
        String reason = null;
        HttpStatus httpStatus = HttpStatus.CREATED;
        Participant participant = null;
        if (bindingResult.hasErrors()) {
            status = "Fail";
            reason = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            ValidatorResponse response = participantValidator.validate(participantDto);
            if (!response.isStatus()) {
                status = "Fail";
                reason = response.getMessage();
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                participant = participantService.createParticipant(participantDto);
            }
        }
        String reasonOrID;
        if ("Fail".equals(status)) {
            reasonOrID = "\"reason\":  \"" + reason + "\",\n";
        } else {
            reasonOrID = "\"id\":  \"" + participant.getId() + "\",\n";
        }

        return new ResponseEntity<>("{\n" +
                "\"status\":  \"" + status + "\",\n" +
                reasonOrID +
                "}", httpStatus);

    }
}
