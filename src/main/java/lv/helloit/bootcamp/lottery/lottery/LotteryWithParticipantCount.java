package lv.helloit.bootcamp.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LotteryWithParticipantCount {
    private Long id;
    private String title;
    private int limit;
    private LocalDate startDate;
    private LocalDate endDate;
    private int participants;

    public LotteryWithParticipantCount(Lottery lottery, int participants) {
        this.id = lottery.getId();
        this.title = lottery.getTitle();
        this.limit = lottery.getLimit();
        this.startDate = lottery.getStartDate();
        this.endDate = lottery.getEndDate();
        this.participants = participants;
    }
}


