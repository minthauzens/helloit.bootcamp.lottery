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
                .lotteryId(participantDto.getLotteryId())
                .registrationDate(LocalDate.now())
                .build();
        return this.participantDao.save(participant);
    }

    public boolean existsByCodeAndLotteryId(String code, Long lotteryId){
        return participantDao.existsByCodeAndLotteryId(code, lotteryId);
    }

    public int countParticipantsByLotteryId(Long lotteryId) {
        return this.participantDao.countParticipantsByLotteryId(lotteryId);
    }
}
