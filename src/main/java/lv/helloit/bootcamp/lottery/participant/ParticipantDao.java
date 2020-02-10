package lv.helloit.bootcamp.lottery.participant;

import org.springframework.data.repository.CrudRepository;

public interface ParticipantDao extends CrudRepository<Participant, String> {
    boolean existsByCodeAndLotteryId(String code, Long lottery_Id);
    int countParticipantsByLotteryId(Long lottery_Id);
}
