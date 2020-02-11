package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class ParticipantValidator {
    private final ParticipantService participantService;
    private final LotteryService lotteryService;

    private ParticipantRegisterDto participantRegisterDto;
    private Lottery lottery;
    ValidatorResponse response;

    public ParticipantValidator(ParticipantService participantService, LotteryService lotteryService) {
        this.participantService = participantService;
        this.lotteryService = lotteryService;
    }

    public ValidatorResponse validate(ParticipantRegisterDto participantDto) {
        response = new ValidatorResponse();
        setParticipantRegisterDto(participantDto);

        if (!isLotteryIdValid())
            response.setStatusFalseWithMessage("Please provide participant with valid lottery id");
        else if (!isAgeAtLeast21())
            response.setStatusFalseWithMessage("Participant has to be with over 21 to participate");
        else if (!isValidCode())
            return response; // returns here! to hide empty if warning
        else if (isLotteryLimitReached())
            response.setStatusFalseWithMessage("Lottery has reached its participant limit");
        else if (hasLotteryEnded())
            response.setStatusFalseWithMessage("Lottery registration period has ended");
        return response;
    }

    private boolean hasLotteryEnded() {
        // lottery ended when endDate is set;
        return (this.lottery.getEndDate() != null);
    }

    private boolean isLotteryLimitReached() {
        // lottery defined in isValidCode()
        int count = this.participantService.countParticipantsByLotteryId(this.lottery.getId());
        int limit = this.lottery.getLimit();
        if (count > limit) {
            throw new RuntimeException("PARTICIPANT COUNT CANT BE LARGER THAN LIMIT!");
            // what happens if limit is 0?
            // cant happen because lotteryRegisterDto @min(value = 1)
        } else return count == limit;
    }

    private boolean isLotteryIdValid() {
        return this.lotteryService.existsById(this.participantRegisterDto.getLotteryId());
    }

    private boolean
    isAgeAtLeast21() {
        return participantRegisterDto.getAge() >= 21;
    }

    private boolean isValidCode() {
        if (!isLength16()) response.setStatusFalseWithMessage("Code has to be 16 digits long");
        else if (!isFirstHalfValid()) response.setStatusFalseWithMessage("Please provide valid code");
        else if (participantService.existsByCodeAndLotteryId(participantRegisterDto.getCode(),
                participantRegisterDto.getLotteryId()))
            response.setStatusFalseWithMessage("Code already has been registered");
        return response.isStatus();
    }

    private boolean isLength16() {
        return this.participantRegisterDto.getCode().length() == 16;
    }

    private boolean isFirstHalfValid() {
        String generatedCode = generateFirstHalf();
        String receivedCodeHalf = participantRegisterDto.getCode().substring(0, 8);
        return generatedCode.equals(receivedCodeHalf);
    }

    private String generateFirstHalf() {
        Optional<Lottery> optionalLottery = lotteryService.getById(participantRegisterDto.getLotteryId());
        // already tested if exists in existsById()
        if (optionalLottery.isEmpty()) {
            throw new RuntimeException("no valid lottery id provided");
        }
        this.lottery = optionalLottery.get();
        String lotteryStartDate = this.lottery.getStartDate().format(DateTimeFormatter.ofPattern("ddMMYY"));

        // email was validated to max length 99, so email length max length 2 digits;
        String emailLength = String.format("%0,2d", participantRegisterDto.getEmail().length());
        if (emailLength.length() > 2) {
            throw new RuntimeException("Email length cant be longer than 2 digits");
        }
        return lotteryStartDate + emailLength;
    }

    public void setParticipantRegisterDto(ParticipantRegisterDto participantRegisterDto) {
        this.participantRegisterDto = participantRegisterDto;
    }


}
