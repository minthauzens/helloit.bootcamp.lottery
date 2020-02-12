package lv.helloit.bootcamp.lottery.lottery;

import lv.helloit.bootcamp.lottery.participant.Participant;
import lv.helloit.bootcamp.lottery.participant.ParticipantService;
import lv.helloit.bootcamp.lottery.utils.ValidatorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LotteryValidatorTest {
    LotteryValidator victim;

    @Mock
    LotteryService lotteryService;
    @Mock
    ParticipantService participantService;

    @BeforeEach
    void setUp() {
        victim = new LotteryValidator(lotteryService, participantService);
    }

    @Test
    void shouldPassRegistrationValidation() {
        LotteryRegistrationDto lotteryRegistrationDto = LotteryRegistrationDto.builder()
                .limit(1)
                .title("Lottery registration validation test")
                .build();

        when(lotteryService.existsByTitle("Lottery registration validation test")).thenReturn(false);

        ValidatorResponse response = victim.validateForRegistration(lotteryRegistrationDto);
        assertTrue(response.isStatus());
        assertNull(response.getMessage());
    }

    @Test
    void shouldFailWhenTitleAlreadyInDb() {
        LotteryRegistrationDto lotteryRegistrationDto = LotteryRegistrationDto.builder()
                .limit(1)
                .title("Lottery registration validation test")
                .build();

        when(lotteryService.existsByTitle("Lottery registration validation test")).thenReturn(true);

        ValidatorResponse response = victim.validateForRegistration(lotteryRegistrationDto);
        assertFalse(response.isStatus());
        assertEquals("Lottery with such title already exists", response.getMessage());
    }

    @Test
    void shouldPassStopRegistrationValidation() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Lottery registration validation test")
                .limit(5)
                .startDate(LocalDate.now())
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.of(lottery));

        ValidatorResponse response = victim.validateForStopRegistration(lotteryIdDto);
        assertTrue(response.isStatus());
        assertNull(response.getMessage());
    }

    @Test
    void shouldFailStopRegistrationValidationWhenLotteryIdDoesntExist() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.empty());

        ValidatorResponse response = victim.validateForStopRegistration(lotteryIdDto);
        assertFalse(response.isStatus());
        assertEquals("Lottery with Id 1 doesn't exist", response.getMessage());
    }

    @Test
    void shouldFailWhenLotteryAlreadyStopped() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Lottery registration validation test")
                .limit(5)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.of(lottery));

        ValidatorResponse response = victim.validateForStopRegistration(lotteryIdDto);
        assertFalse(response.isStatus());
        assertEquals("Lottery registration was already stopped at " + LocalDate.now(), response.getMessage());
    }

    @Test
    void shouldPassChooseWinnerValidation() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Lottery registration validation test")
                .limit(5)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.of(lottery));
        when(participantService.findLotteryWinner(1L)).thenReturn(Optional.empty());

        ValidatorResponse response = victim.validateForChooseWinner(lotteryIdDto);
        assertTrue(response.isStatus());
        assertNull(response.getMessage());
    }

    @Test
    void shouldFailWhenWinnerHasAlreadyBeenChosen() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Lottery registration validation test")
                .limit(5)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.of(lottery));
        when(participantService.findLotteryWinner(1L)).thenReturn(Optional.of(new Participant()));

        ValidatorResponse response = victim.validateForChooseWinner(lotteryIdDto);
        assertFalse(response.isStatus());
        assertEquals("Lottery already has a winner", response.getMessage());
    }

    @Test
    void shouldFailWhenLotteryRegistrationHasNotBeenEnded() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        Lottery lottery = Lottery.builder()
                .id(1L)
                .title("Lottery registration validation test")
                .limit(5)
                .startDate(LocalDate.now())
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.of(lottery));
        when(participantService.findLotteryWinner(1L)).thenReturn(Optional.empty());

        ValidatorResponse response = victim.validateForChooseWinner(lotteryIdDto);
        assertFalse(response.isStatus());
        assertEquals("Lottery registration hasn't been stopped!", response.getMessage());
    }

    @Test
    void shouldFailChooseWinnerValidationWhenLotteryIdDoesntExist() {
        LotteryIdDto lotteryIdDto = LotteryIdDto.builder()
                .id(1L)
                .build();
        when(lotteryService.findById(1L)).thenReturn(Optional.empty());

        ValidatorResponse response = victim.validateForChooseWinner(lotteryIdDto);
        assertFalse(response.isStatus());
        assertEquals("Lottery with Id 1 doesn't exist", response.getMessage());
    }
}
