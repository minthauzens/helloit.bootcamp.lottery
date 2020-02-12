package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LotteryService {
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
        return lotteryDao.save(lottery);
    }

    public List<LotteryWithParticipantCountDto> getAllWithParticipantCount() {
        List<LotteryWithParticipantCountDto> result = new ArrayList<>();
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
        this.lotteryDao.updateEndDateById(id);
    }

    public void setLotteryCompleted(Long id) {
        Optional<Lottery> optionalLottery = this.lotteryDao.findById(id);
        if (optionalLottery.isEmpty()) {
            throw new RuntimeException("No such lottery exists");
        }
        Lottery lottery = optionalLottery.get();
        lottery.setCompleted(true);
        lotteryDao.save(lottery);
    }
}
