package lv.helloit.bootcamp.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Lottery {
    @Id
    private Long id;
    @Column
    private String title;
    @Column
    private int limit;
    @Column
    private LocalDateTime startDate;
    @Column
    private LocalDateTime endDate;
}
