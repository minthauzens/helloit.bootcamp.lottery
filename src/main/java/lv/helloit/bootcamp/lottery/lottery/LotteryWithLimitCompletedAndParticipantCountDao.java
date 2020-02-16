package lv.helloit.bootcamp.lottery.lottery;

import org.springframework.data.repository.CrudRepository;

public interface LotteryWithLimitCompletedAndParticipantCountDao extends CrudRepository<LotteryWithLimitCompletedAndParticipantCountDto, Long> {
    Iterable<LotteryWithLimitCompletedAndParticipantCountDto> findAllByOrderByIdAsc();
}
