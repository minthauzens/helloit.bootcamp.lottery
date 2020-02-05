package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantValidatorTest {
    ParticipantValidator victim;

    private ParticipantRegisterDto participantRegisterDto;
    private String validCode;

    @Mock
    ParticipantService participantService;
    @Mock
    LotteryService lotteryService;

    @BeforeEach
    void setUp() {
        victim = new ParticipantValidator(participantService, lotteryService);

        // valid Dto
        // lottery id now, and start date LocalDate.now()
        String email = "some@mail.com";
        validCode = generateValidDtoCode(email);
        participantRegisterDto = ParticipantRegisterDto.builder()
                .lottery_id(1L)
                .email(email)
                .age(21)
                .code(validCode)
                .build();
    }

    @Test
    void shouldValidateAsTrueWhenProvidedWithValidDto() {
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Participant Validator Test 1")
                .startDate(LocalDate.now())
                .limit(5)
                .build();
        // simulation that lottery with such id exists
        when(lotteryService.existsById(1L)).thenReturn(true);
        when(lotteryService.getById(1L)).thenReturn(Optional.of(lottery));
        // simulates that code is not in DB
        when(participantService.existsByCodeAndLotteryId(validCode, 1L)).thenReturn(false);

        boolean result = victim.validate(participantRegisterDto);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenProvidedWithCodeOfNot16DigitLength() {
        when(lotteryService.existsById(1L)).thenReturn(true);
        participantRegisterDto.setCode("050220138570178"); // code length 15
        boolean result1 = victim.validate(participantRegisterDto);
        assertFalse(result1);

        participantRegisterDto.setCode("05022013857017821"); // code length 17
        boolean result2 = victim.validate(participantRegisterDto);
        assertFalse(result2);
    }

    @Test
    void shouldReturnFalseWhenNotProvidedWithValidLotteryId() {
        when(lotteryService.existsById(1L)).thenReturn(false);
        boolean result = victim.validate(participantRegisterDto);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenParticipantsAgeLowerThan21() {
        when(lotteryService.existsById(1L)).thenReturn(true);
        participantRegisterDto.setAge(20);
        boolean result = victim.validate(participantRegisterDto);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenSuchCodeAlreadyRegisteredInDatabaseForThisLottery() {
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Participant Validator Test 1")
                .startDate(LocalDate.now())
                .limit(5)
                .build();
        when(lotteryService.existsById(1L)).thenReturn(true);
        when(lotteryService.getById(1L)).thenReturn(Optional.of(lottery));
        // simulates that code is in DB
        when(participantService.existsByCodeAndLotteryId(validCode, 1L)).thenReturn(true);
        boolean result = victim.validate(participantRegisterDto);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenCodeDoesntHaveTheSameDateAsLotteryStartDate() {
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Participant Validator Test 1")
                // now lottery and code have different dates
                // code has today
                .startDate(LocalDate.now().minus(Period.ofDays(1)))
                .limit(5)
                .build();
        when(lotteryService.existsById(1L)).thenReturn(true);
        when(lotteryService.getById(1L)).thenReturn(Optional.of(lottery));
        boolean result = victim.validate(participantRegisterDto);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenEmailLengthIsDifferentFromCode() {
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Participant Validator Test 1")
                .startDate(LocalDate.now())
                .limit(5)
                .build();
        participantRegisterDto.setEmail("somethingVeryLongAndDifferent@tempmailer.com");
        when(lotteryService.existsById(1L)).thenReturn(true);
        when(lotteryService.getById(1L)).thenReturn(Optional.of(lottery));
        boolean result = victim.validate(participantRegisterDto);
        assertFalse(result);
    }

    private String generateValidDtoCode(String email) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMYY"));
        String emailLength = String.format("%0,2d", email.length());
        return date + emailLength + getRandom8DigitNumber();
    }

    private String getRandom8DigitNumber() {
        double min = 10000000;
        double max = 90000000;
        Random random = new Random();
        Double randomNumber = min + random.nextDouble() * max;
        // returns 8 digits without comma
        return String.format("%.0f", randomNumber);
    }
}