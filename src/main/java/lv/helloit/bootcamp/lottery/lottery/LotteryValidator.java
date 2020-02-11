package lv.helloit.bootcamp.lottery.lottery;

import lv.helloit.bootcamp.lottery.ValidatorResponse;
import lv.helloit.bootcamp.lottery.participant.Participant;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
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
        Optional<Lottery> optionalLottery = this.lotteryService.getById(lotteryIdDto.getId());
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
        Optional<Lottery> optionalLottery = this.lotteryService.getById(lotteryIdDto.getId());
        if (optionalLottery.isEmpty()) {
            response.setStatusFalseWithMessage("Lottery with Id " + lotteryIdDto.getId() + " doesn't exist");
            return response;
        }
        if (!isEndDateSet(optionalLottery.get())) {
            response.setStatusFalseWithMessage("Lottery registration hasn't been stopped!");
        }
        doesLotteryHaveAWinner(optionalLottery.get().getId());
        return response;
    }

    private void doesLotteryHaveAWinner(Long id) {
         Optional<Participant> optionalParticipant = participantService.findLotteryWinner(id);
        if (optionalParticipant.isPresent()) {
            response.setStatusFalseWithMessage("Lottery already has a winner");
        }
    }

    private boolean isEndDateSet(Lottery lottery) {
        return lottery.getEndDate() != null;
    }
}
