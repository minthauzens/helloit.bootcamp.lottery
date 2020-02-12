package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ParticipantService {
    private final ParticipantDao participantDao;
    private final LotteryService lotteryService;

    public ParticipantService(ParticipantDao participantDao, LotteryService lotteryService) {
        this.participantDao = participantDao;
        this.lotteryService = lotteryService;
    }


    public long createParticipant(ParticipantRegisterDto participantDto) {
        Participant participant = Participant.builder()
                .email(participantDto.getEmail())
                .code(participantDto.getCode())
                .age(participantDto.getAge())
                .lotteryId(participantDto.getLotteryId())
                .registrationDate(LocalDate.now())
                .build();
        Participant result = this.participantDao.save(participant);
        return result.getId();
    }

    public boolean existsByCodeAndLotteryId(String code, Long lotteryId) {
        return participantDao.existsByCodeAndLotteryId(code, lotteryId);
    }

    public int countParticipantsByLotteryId(Long lotteryId) {
        return this.participantDao.countParticipantsByLotteryId(lotteryId);
    }

    public Participant chooseLotteryWinner(Long lotteryId) {
        // lottery has to be stopped/closed before winner can be chosen
        Participant participant = participantDao.findFirstByLotteryIdOrderByRandom(lotteryId);
        setAndSaveWinnerStatus(participant);
        return participant;
    }

    private void setAndSaveWinnerStatus(Participant participant) {
        participant.setWinner(true);
        participantDao.save(participant);
    }

    public Optional<Participant> findLotteryWinner(Long lotteryId) {
        return participantDao.findByWinnerAndLotteryId(true, lotteryId);
    }

    public Optional<Participant> findByEmailAndCodeAndLotteryId(ParticipantStatusDto participantStatusDto) {
        return this.participantDao.findByEmailAndCodeAndLotteryId(participantStatusDto.getEmail(),
                participantStatusDto.getCode(),
                participantStatusDto.getLotteryId());
    }

    public ResponseEntity<String> getParticipantStatus(ParticipantStatusDto participantStatusDto) {
        Optional<Participant> optionalParticipant = findByEmailAndCodeAndLotteryId(participantStatusDto);
        if (optionalParticipant.isEmpty()) {
            return new ResponseEntity<>("\"status\": \"ERROR\"", HttpStatus.BAD_REQUEST);
        }
        Participant participant = optionalParticipant.get();
        Optional<Lottery> optionalLottery = this.lotteryService.findById(participant.getLotteryId());
        if (optionalLottery.isEmpty()) {
            throw new RuntimeException("DB Error, got participant with invalid lottery id");
        }
        if (!optionalLottery.get().isCompleted()) {
            return new ResponseEntity<>("\"status\": \"PENDING\"", HttpStatus.OK);
        }
        String status = (participant.isWinner()) ? "WIN" : "LOOSE";
        return new ResponseEntity<>("\"status\": \"" + status + "\"", HttpStatus.OK);
    }
}
