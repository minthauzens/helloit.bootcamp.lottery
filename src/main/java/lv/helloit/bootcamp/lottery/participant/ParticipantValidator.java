package lv.helloit.bootcamp.lottery.participant;

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

    public ParticipantValidator(ParticipantService participantService, LotteryService lotteryService) {
        this.participantService = participantService;
        this.lotteryService = lotteryService;
    }

    public boolean validate(ParticipantRegisterDto participantDto) {
        setParticipantRegisterDto(participantDto);
        return this.lotteryService.existsById(this.participantRegisterDto.getLottery_id()) &&
                isAgeAtLeast21() &&
                isValidCode();
    }

    private boolean
    isAgeAtLeast21() {
        return participantRegisterDto.getAge() >= 21;
    }

    private boolean isValidCode() {
        return isLength16() &&
                isFirstHalfValid() &&
                !participantService.existsByCodeAndLotteryId(participantRegisterDto.getCode(),
                        participantRegisterDto.getLottery_id());
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
        Optional<Lottery> optionalLottery = lotteryService.getById(participantRegisterDto.getLottery_id());
        // already tested if exists in existsById
        if (optionalLottery.isEmpty()) {
            throw new RuntimeException("no valid lottery id provided");
        }
        String lotteryStartDate = optionalLottery.get().getStartDate().format(DateTimeFormatter.ofPattern("ddMMYY"));

        // email  was validated to max length 99, so email length max length 2 digits;
        String emailLength = String.format("%0,2d", participantRegisterDto.getEmail().length());

        return lotteryStartDate + emailLength;
    }

    public void setParticipantRegisterDto(ParticipantRegisterDto participantRegisterDto) {
        this.participantRegisterDto = participantRegisterDto;
    }


}
