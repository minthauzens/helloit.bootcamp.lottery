package lv.helloit.bootcamp.lottery.participant;

import org.springframework.stereotype.Component;

@Component
public class ParticipantValidator {
    public boolean validate(ParticipantRegisterDto participantDto) {
        // TODO: implement ME!

        // test age (tho this has been already done)
        // test email ( for uniqueness)
        // test code
        return true;
    }
}
