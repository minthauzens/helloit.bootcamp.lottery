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
        if (bindingResult.hasErrors() || !(participantValidator.validate(participantDto))) {
            return new ResponseEntity<>("{\n" +
                    "\"status\":  \"Fail\",\n" +
                    "\"reason\": \"Please provide valid participant\"\n" +
                    "}", HttpStatus.BAD_REQUEST);

        } else {
            Participant participant = participantService.createParticipant(participantDto);
            return new ResponseEntity<>("{\n" +
                    "\"status\":  \"OK\",\n" +
                    "\"id\": " + participant.getId() + "\n" +
                    "}", HttpStatus.CREATED);
        }

    }
}
