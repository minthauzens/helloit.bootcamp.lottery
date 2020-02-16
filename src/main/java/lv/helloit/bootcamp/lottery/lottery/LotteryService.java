package lv.helloit.bootcamp.lottery.lottery;

import lombok.extern.slf4j.Slf4j;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class LotteryService {
    private final LotteryDao lotteryDao;
    private final LotteryWithParticipantCountDao lotteryWithParticipantCountDao;
    private final ParticipantService participantService;
    private final LotteryWithLimitCompletedAndParticipantCountDao lotteryWithLimitCompletedAndParticipantCountDao;

    public LotteryService(LotteryDao lotteryDao,
                          LotteryWithParticipantCountDao lotteryWithParticipantCountDao,
                          LotteryWithLimitCompletedAndParticipantCountDao lotteryWithLimitCompletedAndParticipantCountDao,
                          ParticipantService participantService) {
        this.lotteryDao = lotteryDao;
        this.lotteryWithParticipantCountDao = lotteryWithParticipantCountDao;
        this.participantService = participantService;
        this.lotteryWithLimitCompletedAndParticipantCountDao = lotteryWithLimitCompletedAndParticipantCountDao;
    }

    public Lottery createLottery(LotteryRegistrationDto lotteryRegistrationDto) {
        Lottery lottery = Lottery.builder()
                .title(lotteryRegistrationDto.getTitle())
                .limit(lotteryRegistrationDto.getLimit())
                .startDate(LocalDate.now())
                .build();
        log.info("Creating: " + lottery);
        return lotteryDao.save(lottery);
    }

    public ArrayList<LotteryWithParticipantCountDto> getAllWithParticipantCount() {
        ArrayList<LotteryWithParticipantCountDto> result = new ArrayList<>();
        lotteryWithParticipantCountDao.findAll().forEach(result::add);
        return result;
    }

    public boolean existsById(Long id) {
        return this.lotteryDao.existsById(id);
    }

    public Optional<Lottery> findById(Long id) {
        return this.lotteryDao.findById(id);
    }

    public void stopRegistration(Long id) {
        Optional<Lottery> optionalLottery = this.lotteryDao.findById(id);
        if (optionalLottery.isEmpty()) {
            log.error("Lottery with such id doesn't exist, should have been already tested for it in validator");
            throw new RuntimeException("Lottery with such id doesn't exist");
        }
        Lottery lottery = optionalLottery.get();
        lottery.setEndDate(LocalDate.now());
        log.info("Stopping registration: Lottery.id = " + lottery.getId());
        if (!(this.participantService.existsByLotteryId(id))) {
            log.info("This lottery has no participants, setting it completed");
            setLotteryCompleted(id);
        }
        this.lotteryDao.save(lottery);
    }

    public void setLotteryCompleted(Long id) {
        Optional<Lottery> optionalLottery = this.lotteryDao.findById(id);
        if (optionalLottery.isEmpty()) {
            throw new RuntimeException("No such lottery exists");
        }
        Lottery lottery = optionalLottery.get();
        lottery.setCompleted(true);
        log.info("Setting completed: Lottery.id = " + lottery.getId());
        lotteryDao.save(lottery);
    }

    public boolean existsByTitle(String title) {
        return this.lotteryDao.existsByTitle(title);
    }

    public ArrayList<LotteryWithLimitCompletedAndParticipantCountDto> findAllWithParticipantCount() {
        ArrayList<LotteryWithLimitCompletedAndParticipantCountDto> result = new ArrayList<>();
        lotteryWithLimitCompletedAndParticipantCountDao.findAllByOrderByIdAsc().forEach(result::add);
        return result;
    }
}
