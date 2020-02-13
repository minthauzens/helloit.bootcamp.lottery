package lv.helloit.bootcamp.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LotteryRegistrationDto {
    @NotBlank(message = "title cant be blank")
    private String title;
    @NotNull(message = "limit has to be provided")
    @Max(value = Integer.MAX_VALUE, message = "Cant be larger than Integer.MAX_VALUE")
    @Min(value = 1, message = "Has to be an int and larger than 0")
    private int limit;

}
