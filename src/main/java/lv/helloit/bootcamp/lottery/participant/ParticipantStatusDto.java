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
public class ParticipantStatusDto {
    @NotNull(message = "Lottery id must be provided")
    @JsonProperty("id")
    private Long lotteryId;

    @NotNull(message = "email has to be provided")
    @Email(message = "must provide valid email")
    @Size(min = 5, max = 99, message = "email name length can't exceed 99")
    private String email;

    @NotBlank(message = "code has to be provided")
    @Digits(integer = 16, fraction = 0, message = "The code must be consisting of 16 digits")
    private String code;
}
