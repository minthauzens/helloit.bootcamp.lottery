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

import java.util.Optional;

import static lv.helloit.bootcamp.lottery.participant.ParticipantNumberGenerator.*;
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

    @BeforeEach
    void setUp() {
        setTestingLottery();
    }

//    @BeforeAll
//    static void beforeAll() {
//        LotteryRegistrationDto lotteryRegistrationDto = LotteryRegistrationDto.builder()
//                .title("Participant Lottery SQL 1")
//                .limit(5)
//                .build();
//        this.lotteryService.createLottery(lotteryRegistrationDto);
//    }
//    BEFORE ALL STATIC WHY?

    private void setTestingLottery() {
        Optional<Lottery> optional = this.lotteryDao.findFirstByTitle("Participant Lottery SQL 1");
        if (optional.isPresent()) {
            this.lottery = optional.get();
        } else {
            createLottery();
            setTestingLottery();
        }
    }

    private void createLottery() {
        LotteryRegistrationDto lotteryRegistrationDto = LotteryRegistrationDto.builder()
                .title("Participant Lottery SQL 1")
                .limit(5)
                .build();
        this.lotteryService.createLottery(lotteryRegistrationDto);
    }

    @Test
    void shouldRegisterParticipant() throws Exception {
        String json = getJsonToRegisterParticipant(1);
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();
    }

    @Test
    void shouldReturnFalseWhenSuchCodeAlreadyRegistered() throws Exception {
        registerParticipant(1);
        String json = getJsonToRegisterParticipant(1);
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
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
            registerParticipant(i);
        }
        String json = getJsonToRegisterParticipant(6);
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery has reached its participant limit"))
                .andReturn();
    }

    private void registerParticipant(int number) throws Exception {
        String json = getJsonToRegisterParticipant(number);
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
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