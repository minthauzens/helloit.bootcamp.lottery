package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LotteryDao extends CrudRepository<Lottery, Long> {
    Optional<Lottery> findFirstByTitle(String title);

    boolean existsByTitle(String title);
}
