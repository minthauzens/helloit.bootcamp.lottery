package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.data.repository.CrudRepository;

public interface LotteryWithParticipantCountDao extends CrudRepository<LotteryWithParticipantCountDto, Long> {
}
