package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LotteryService {
    private final LotteryDao lotteryDao;

    public LotteryService(LotteryDao lotteryDao) {
        this.lotteryDao = lotteryDao;
    }

    public Lottery createLottery(LotteryRegistrationDto lotteryRegistrationDto) {
        Lottery lottery = new Lottery();
        lottery.setTitle(lotteryRegistrationDto.getTitle());
        lottery.setLimit(lotteryRegistrationDto.getLimit());
        lottery.setStartDate(LocalDate.now());
        lottery = lotteryDao.save(lottery);
        return lottery;
    }

    public List<Lottery> getAllLotteries() {
        List<Lottery> result = new ArrayList<>();
        for (Lottery lottery : lotteryDao.findAll()) {
            result.add(lottery);
        }
        return result;
    }

}
