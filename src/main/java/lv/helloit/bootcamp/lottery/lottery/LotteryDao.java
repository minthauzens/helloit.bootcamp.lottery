package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.data.repository.CrudRepository;

public interface LotteryDao extends CrudRepository<Lottery, String> {
}
