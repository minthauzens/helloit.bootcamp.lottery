package lv.helloit.bootcamp.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LotteryRegistrationDto {
    @NotBlank
    private String title;
    @NotBlank
    @Max(Integer.MAX_VALUE)
    private int limit;

}
