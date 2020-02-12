package lv.helloit.bootcamp.lottery.participant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantStatusDto {
    @NotNull(message = "Lottery id must be provided")
    @JsonProperty("id")
    private Long lotteryId;

    @Email(message = "must provide valid email")
    @Size(min = 5, max = 99, message = "email name length can't exceed 99")
    private String email;

    @NotNull(message = "code has to be provided")
    @Digits(integer = 16, fraction = 0, message = "The code must be consisting of 16 digits")
    private String code;
}
