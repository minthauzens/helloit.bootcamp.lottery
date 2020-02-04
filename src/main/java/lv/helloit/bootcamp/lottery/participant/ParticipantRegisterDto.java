package lv.helloit.bootcamp.lottery.participant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantRegisterDto {
    @NotNull(message = "Lottery id must be provided")
    private Long id;
    private String email;
    private int age;
    private String code;
}
