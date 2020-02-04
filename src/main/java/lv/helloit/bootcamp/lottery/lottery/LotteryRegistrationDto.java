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
    @NotBlank
    private String title;
    @NotNull
    @Max(Integer.MAX_VALUE)
    @Min(1)
    private int limit;

}
