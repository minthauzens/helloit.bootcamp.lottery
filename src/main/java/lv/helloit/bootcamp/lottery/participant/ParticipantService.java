package lv.helloit.bootcamp.lottery.participant;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ParticipantService {
    private final ParticipantDao participantDao;

    public ParticipantService(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }


    public Participant createParticipant(ParticipantRegisterDto participantDto) {
        Participant participant = Participant.builder()
                .email(participantDto.getEmail())
                .code(participantDto.getCode())
                .age(participantDto.getAge())
                .lottery_id(participantDto.getLottery_id())
                .registrationDate(LocalDate.now())
                .build();
        return this.participantDao.save(participant);
    }
}
