package lv.helloit.bootcamp.lottery.participant;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParticipantDao extends CrudRepository<Participant, String> {
    boolean existsByCodeAndLotteryId(String code, Long lottery_Id);

    int countParticipantsByLotteryId(Long lottery_Id);

    @Query(value = "SELECT * FROM participant WHERE lottery_id = :lotteryId ORDER BY random() LIMIT 1", nativeQuery = true)
    Participant findFirstByLotteryIdOrderByRandom(@Param("lotteryId") long lotteryId);

    Optional<Participant> findByWinnerAndLotteryId(boolean winner, Long lotteryId);

    Optional<Participant> findByEmailAndCodeAndLotteryId(String email, String code, Long lotteryId);

    boolean existsByLotteryId(Long lotteryId);
}
