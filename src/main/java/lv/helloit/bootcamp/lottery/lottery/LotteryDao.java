package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface LotteryDao extends CrudRepository<Lottery, Long> {
    Optional<Lottery> findFirstByTitle(String title);

    @Query(value = "update lottery set end_date = now() where lottery.id = :id", nativeQuery = true)
    void updateEndDateById(@Param("id") Long id);
}
