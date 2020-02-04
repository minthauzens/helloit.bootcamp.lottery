package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public HashMap<Long, Lottery> getAllLotteries() {
        HashMap<Long, Lottery> result = new HashMap<Long, Lottery>();
        for (Lottery lottery : lotteryDao.findAll()) {
            result.put(lottery.getId(), lottery);
        }
        return result;
    }

}
