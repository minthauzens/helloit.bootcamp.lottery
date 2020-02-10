package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LotteryService {
    private final LotteryDao lotteryDao;

    public LotteryService(LotteryDao lotteryDao) {
        this.lotteryDao = lotteryDao;
    }

    public Lottery createLottery(LotteryRegistrationDto lotteryRegistrationDto) {
        Lottery lottery = Lottery.builder()
                .title(lotteryRegistrationDto.getTitle())
                .limit(lotteryRegistrationDto.getLimit())
                .startDate(LocalDate.now())
                .build();
        return lotteryDao.save(lottery);
    }

    public List<Lottery> getAll() {
        List<Lottery> result = new ArrayList<>();
        lotteryDao.findAll().forEach(result::add);
        return result;
    }

    public boolean existsById(Long id) {
        return this.lotteryDao.existsById(id);
    }

    public Optional<Lottery> getById(Long id) {
        return this.lotteryDao.findById(id);
    }
}
