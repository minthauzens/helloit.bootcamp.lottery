package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryDao;
import lv.helloit.bootcamp.lottery.lottery.LotteryRegistrationDto;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static lv.helloit.bootcamp.lottery.participant.ParticipantNumberGenerator.generateValidDtoCodeFirstHalf;
import static lv.helloit.bootcamp.lottery.participant.ParticipantNumberGenerator.longTo8digitString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test-sqls/schema-base.sql")
class ParticipantRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    LotteryDao lotteryDao;
    @Autowired
    LotteryService lotteryService;

    private Lottery lottery;
    private int counter = 0;

    @BeforeEach
    void setUp() {
        // new for each test, so tests doesnt impact each other
        setTestingLottery();
    }

    private void setTestingLottery() {
        counter++;
        Optional<Lottery> optional = this.lotteryDao.findFirstByTitle("Participant Lottery SQL " + counter);
        this.lottery = optional.orElseGet(() -> createLottery(counter));
    }

    private Lottery createLottery(int number) {
        LotteryRegistrationDto lotteryRegistrationDto = LotteryRegistrationDto.builder()
                .title("Participant Lottery SQL " + number)
                .limit(5)
                .build();
        return this.lotteryService.createLottery(lotteryRegistrationDto);
    }

    @Test
    void shouldRegisterParticipant() throws Exception {
        String json = getJsonToRegisterParticipant(counter);
        performPost(json, "/register")
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();
    }

    @Test
    void shouldReturnFalseWhenSuchCodeAlreadyRegistered() throws Exception {
        registerParticipant()
                .andExpect(status().isCreated())
                .andReturn();
        registerParticipant()
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Code already has been registered"))
                .andReturn();
    }

    @Test
    void shouldReturnFalseWhenParticipantLimitReached() throws Exception {
        // base Lottery has limit of 5
        for (int i = 0; i < 5; i++) {
            registerParticipant(i)
                    .andExpect(status().isCreated())
                    .andReturn();
        }
        registerParticipant(6)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery has reached its participant limit"))
                .andReturn();
    }

    @Test
    void shouldFailWhenLotteryRegistrationPeriodHasBeenEnded() throws Exception {
        this.lotteryService.stopRegistration(lottery.getId());

        registerParticipant()
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery registration period has ended"))
                .andReturn();
    }

    @Test
    void shouldChooseLotteryWinner() throws Exception {
        for (int i = 0; i < 2; i++) {
            registerParticipant(i);
        }
        this.lotteryService.stopRegistration(lottery.getId());

        String json = "{\"id\": \"" + this.lottery.getId() + "\"\n}";

        performPost(json, "/choose-winner")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.winnerCode").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.reason").doesNotExist())
                .andReturn();
    }

    @Test
    void shouldFailChoosingWinnerWhenLotteryHasNotBeenStopped() throws Exception {
        for (int i = 0; i < 2; i++) {
            registerParticipant(i);
        }

        String json = "{\"id\": \"" + this.lottery.getId() + "\"\n}";

        performPost(json, "/choose-winner")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.winnerCode").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery registration hasn't been stopped!"))
                .andReturn();
    }
    @Test
    void shouldFailChooseWinnerWhenWinnerAlreadyChosen() throws Exception {
        for (int i = 0; i < 2; i++) {
            registerParticipant(i);
        }
        this.lotteryService.stopRegistration(this.lottery.getId());
        String json = "{\"id\": \"" + this.lottery.getId() + "\"\n}";

        performPost(json, "/choose-winner").andReturn();

        performPost(json, "/choose-winner")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.winnerCode").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery already has a winner"))
                .andReturn();
    }

    @Test
    void shouldFailChooseWinnerWhenLotteryHasNoParticipants() throws Exception {
        this.lotteryService.stopRegistration(this.lottery.getId());
        String json = "{\"id\": \"" + this.lottery.getId() + "\"\n}";

        performPost(json, "/choose-winner")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.winnerCode").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("This lottery has no participants!"))
                .andReturn();

    }

    private ResultActions performPost(String json, String url) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions registerParticipant(int number) throws Exception {
        String json = getJsonToRegisterParticipant(number);
        return performPost(json, "/register");
    }

    private ResultActions registerParticipant() throws Exception {
        return registerParticipant(counter);
    }

    private String getJsonToRegisterParticipant(int number) {
        String participantEmail = number + "some@mail.com";
        String dtoCodeFirstHalf = generateValidDtoCodeFirstHalf(participantEmail, this.lottery.getStartDate());
        String secondHalf = longTo8digitString(number);
        String dtoCode = dtoCodeFirstHalf + secondHalf;
        return "{\n" +
                "    \"email\": \"" + participantEmail + "\",\n" +
                "    \"age\": 21,\n" +
                "    \"code\": \"" + dtoCode + "\",\n" +
                "    \"id\": \"" + this.lottery.getId() + "\"\n" + // id -> meaning lottery_id
                "}";
    }
}