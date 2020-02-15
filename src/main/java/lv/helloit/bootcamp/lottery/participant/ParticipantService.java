package lv.helloit.bootcamp.lottery.participant;

import lombok.extern.slf4j.Slf4j;
import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import lv.helloit.bootcamp.lottery.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
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
        log.info("Saving: " + participant);
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
        participant.setWinner(true);
        log.info("Participant (id = " + participant.getId() + ") set winner for Lottery " + participant.getLotteryId());
        participantDao.save(participant);
        return participant;
    }

    public Optional<Participant> findLotteryWinner(Long lotteryId) {
        return participantDao.findByWinnerAndLotteryId(true, lotteryId);
    }

    public Optional<Participant> findByEmailAndCodeAndLotteryId(ParticipantStatusDto participantStatusDto) {
        return this.participantDao.findByEmailAndCodeAndLotteryId(participantStatusDto.getEmail(),
                participantStatusDto.getCode(),
                participantStatusDto.getLotteryId());
    }

    public Response getParticipantStatus(ParticipantStatusDto participantStatusDto) {
        Optional<Participant> optionalParticipant = findByEmailAndCodeAndLotteryId(participantStatusDto);
        if (optionalParticipant.isEmpty()) {
            String message = "Participant doesn't exist in DB; " + participantStatusDto;
            log.info(message);
            return new Response(false, message, HttpStatus.BAD_REQUEST);
        }
        Participant participant = optionalParticipant.get();
        Optional<Lottery> optionalLottery = this.lotteryService.findById(participant.getLotteryId());
        if (optionalLottery.isEmpty()) {
            log.error("DB error, participant with invalid lottery id");
            throw new RuntimeException("DB Error, got participant with invalid lottery id");
        }
        if (!optionalLottery.get().isCompleted()) {
            String message = "Lottery hasn't been completed, participant status - pending";
            log.info(message);
            return new Response(true,"PENDING", HttpStatus.OK);
        }
        String participantStatus = (participant.isWinner()) ? "WIN" : "LOOSE";
        log.info("the participant status: " + participantStatus);
        return new Response(true, participantStatus, HttpStatus.OK);
    }

    public boolean existsByLotteryId(Long lotteryId) {
        return this.participantDao.existsByLotteryId(lotteryId);
    }
}
