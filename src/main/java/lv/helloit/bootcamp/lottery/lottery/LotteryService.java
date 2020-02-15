package lv.helloit.bootcamp.lottery.lottery;

import lv.helloit.bootcamp.lottery.participant.ParticipantRestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class LotteryService {
    private static final Logger LOGGER = LogManager.getLogger(LotteryService.class);
    private final LotteryDao lotteryDao;
    private final LotteryWithParticipantCountDao lotteryWithParticipantCountDao;

    public LotteryService(LotteryDao lotteryDao, LotteryWithParticipantCountDao lotteryWithParticipantCountDao) {
        this.lotteryDao = lotteryDao;
        this.lotteryWithParticipantCountDao = lotteryWithParticipantCountDao;
    }

    public Lottery createLottery(LotteryRegistrationDto lotteryRegistrationDto) {
        Lottery lottery = Lottery.builder()
                .title(lotteryRegistrationDto.getTitle())
                .limit(lotteryRegistrationDto.getLimit())
                .startDate(LocalDate.now())
                .build();
        LOGGER.info("Saving: " + lottery);
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
            LOGGER.error("Lottery with such id doesn't exist, should have been already tested for it in validator");
            throw new RuntimeException("Lottery with such id doesn't exist");
        }
        Lottery lottery = optionalLottery.get();
        lottery.setEndDate(LocalDate.now());
        LOGGER.info("Stopping registration: Lottery.id = " + lottery.getId());
        this.lotteryDao.save(lottery);
    }

    public void setLotteryCompleted(Long id) {
        Optional<Lottery> optionalLottery = this.lotteryDao.findById(id);
        if (optionalLottery.isEmpty()) {
            throw new RuntimeException("No such lottery exists");
        }
        Lottery lottery = optionalLottery.get();
        lottery.setCompleted(true);
        LOGGER.info("Setting completed: Lottery.id = " + lottery.getId());
        lotteryDao.save(lottery);
    }

    public boolean existsByTitle(String title) {
        return this.lotteryDao.existsByTitle(title);
    }
}
