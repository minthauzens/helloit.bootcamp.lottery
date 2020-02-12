package lv.helloit.bootcamp.lottery.lottery;

import lv.helloit.bootcamp.lottery.participant.Participant;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LotteryValidator {
    private final LotteryService lotteryService;
    private final ParticipantService participantService;

    private ValidatorResponse response;

    public LotteryValidator(LotteryService lotteryService, ParticipantService participantService) {
        this.lotteryService = lotteryService;
        this.participantService = participantService;
    }

    public ValidatorResponse validateForStopRegistration(LotteryIdDto lotteryIdDto) {
        response = new ValidatorResponse();
        Optional<Lottery> optionalLottery = this.lotteryService.findById(lotteryIdDto.getId());
        if (optionalLottery.isEmpty()) {
            response.setStatusFalseWithMessage("Lottery with Id " + lotteryIdDto.getId() + " doesn't exist");
            return response;
        }
        if (isEndDateSet(optionalLottery.get())) {
            response.setStatusFalseWithMessage("Lottery registration was already stopped at " + optionalLottery.get().getEndDate());
        }
        return response;
    }

    public ValidatorResponse validateForChooseWinner(LotteryIdDto lotteryIdDto) {
        response = new ValidatorResponse();
        Optional<Lottery> optionalLottery = this.lotteryService.findById(lotteryIdDto.getId());
        if (optionalLottery.isEmpty()) {
            response.setStatusFalseWithMessage("Lottery with Id " + lotteryIdDto.getId() + " doesn't exist");
            return response;
        }
        if (!isEndDateSet(optionalLottery.get())) {
            response.setStatusFalseWithMessage("Lottery registration hasn't been stopped!");
        }
        if (doesLotteryHaveAWinner(optionalLottery.get().getId())) {
            response.setStatusFalseWithMessage("Lottery already has a winner");
        }
        return response;
    }

    private boolean doesLotteryHaveAWinner(Long id) {
        Optional<Participant> optionalParticipant = participantService.findLotteryWinner(id);
        return optionalParticipant.isPresent();
    }

    private boolean isEndDateSet(Lottery lottery) {
        return lottery.getEndDate() != null;
    }
}
