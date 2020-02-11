package lv.helloit.bootcamp.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryIdDto {
    @NotNull(message = "id has to be provided")
    @Min(value = 1, message = "valid id has to be provided")
    private Long id;
}
