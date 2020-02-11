package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static lv.helloit.bootcamp.lottery.participant.ParticipantNumberGenerator.generateValidDtoCode;
import static org.junit.jupiter.api.Assertions.*;
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
                .lotteryId(1L)
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
        when(participantService.countParticipantsByLotteryId(1L)).thenReturn(0);

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertTrue(validatorResponse.isStatus());
        assertNull(validatorResponse.getMessage());
    }

    @Test
    void shouldReturnFalseWhenProvidedWithCodeOfNot16DigitLength() {
        when(lotteryService.existsById(1L)).thenReturn(true);
        participantRegisterDto.setCode("050220138570178"); // code length 15
        boolean result1 = victim.validate(participantRegisterDto).isStatus();
        assertFalse(result1);

        participantRegisterDto.setCode("05022013857017821"); // code length 17

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertFalse(validatorResponse.isStatus());
        assertEquals("Code has to be 16 digits long", validatorResponse.getMessage());
    }

    @Test
    void shouldReturnFalseWhenNotProvidedWithValidLotteryId() {
        when(lotteryService.existsById(1L)).thenReturn(false);

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertFalse(validatorResponse.isStatus());
        assertEquals("Please provide participant with valid lottery id", validatorResponse.getMessage());
    }

    @Test
    void shouldReturnFalseWhenParticipantsAgeLowerThan21() {
        when(lotteryService.existsById(1L)).thenReturn(true);
        participantRegisterDto.setAge(20);

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertFalse(validatorResponse.isStatus());
        assertEquals("Participant has to be with over 21 to participate", validatorResponse.getMessage());
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
        when(participantService.existsByCodeAndLotteryId(validCode, 1L)).thenReturn(true);

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertFalse(validatorResponse.isStatus());
        assertEquals("Code already has been registered", validatorResponse.getMessage());
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

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertFalse(validatorResponse.isStatus());
        assertEquals("Please provide valid code", validatorResponse.getMessage());
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

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);

        assertFalse(validatorResponse.isStatus());
        assertEquals("Please provide valid code", validatorResponse.getMessage());
    }

    @Test
    void shouldReturnFalseWhenLimitIsReached(){
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Participant Validator Test 1")
                .startDate(LocalDate.now())
                .limit(1)
                .build();
        when(lotteryService.existsById(1L)).thenReturn(true);
        when(lotteryService.getById(1L)).thenReturn(Optional.of(lottery));
        when(participantService.existsByCodeAndLotteryId(validCode, 1L)).thenReturn(false);
        when(participantService.countParticipantsByLotteryId(1L)).thenReturn(1);

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);
        assertFalse(validatorResponse.isStatus());
        assertEquals("Lottery has reached its participant limit", validatorResponse.getMessage());
    }

    @Test
    void shouldReturnFalseWhenEndDateIsSet() {
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Participant Validator Test 1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .limit(2)
                .build();
        when(lotteryService.existsById(1L)).thenReturn(true);
        when(lotteryService.getById(1L)).thenReturn(Optional.of(lottery));
        when(participantService.existsByCodeAndLotteryId(validCode, 1L)).thenReturn(false);
        when(participantService.countParticipantsByLotteryId(1L)).thenReturn(0);

        ValidatorResponse validatorResponse = victim.validate(participantRegisterDto);
        assertFalse(validatorResponse.isStatus());
        assertEquals("Lottery registration period has ended", validatorResponse.getMessage());
    }
}