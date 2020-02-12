package lv.helloit.bootcamp.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "lottery_with_participant_count")
@Immutable
public class LotteryWithParticipantCountDto {
    @Id
    @Column
    private Long id;
    @Column
    private String title;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    private long participants;
}


