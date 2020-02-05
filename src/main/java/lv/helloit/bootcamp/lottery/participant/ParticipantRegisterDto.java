package lv.helloit.bootcamp.lottery.participant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantRegisterDto {
    @NotNull(message = "Lottery id must be provided")
    @JsonProperty("id")
    private Long lottery_id;

    @Email(message = "must provide valid email")
    @Size(min = 5, max=99, message = "email name length can't exceed 99")
    private String email;

    @NotNull(message = "age has to be provided")
    @Min(value = 21, message = "Only people over 21, can participate in lotteries")
    @Max(value = 100, message = "People over 100 should have different priorities")
    private int age;

    @NotNull(message = "code has to be provided")
    @Digits(integer =16,fraction = 0, message = "The code must be consisting of 16 digits")
    private String code;
}
