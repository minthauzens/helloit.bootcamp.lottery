package lv.helloit.bootcamp.lottery.participant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Participant {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private int age;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;
    @Column
    private Long lotteryId;

}
