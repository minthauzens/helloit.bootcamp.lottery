package lv.helloit.bootcamp.lottery.lottery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Lottery {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(name = "participant_limit")
    private int limit;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    @ToString.Exclude
    private boolean completed; // winner has been chosen
}
