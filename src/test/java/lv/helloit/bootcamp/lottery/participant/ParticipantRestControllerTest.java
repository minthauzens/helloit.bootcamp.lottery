package lv.helloit.bootcamp.lottery.participant;

import lv.helloit.bootcamp.lottery.lottery.Lottery;
import lv.helloit.bootcamp.lottery.lottery.LotteryDao;
import lv.helloit.bootcamp.lottery.lottery.LotteryRegistrationDto;
import lv.helloit.bootcamp.lottery.lottery.LotteryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static lv.helloit.bootcamp.lottery.participant.ParticipantTestHelper.generateValidDtoCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test/schema-base.sql")
class ParticipantRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    LotteryDao lotteryDao;
    @Autowired
    LotteryService lotteryService;

    private Lottery lottery;
    private String email;
    private String validCode;

    public ParticipantRestControllerTest() {
        this.email = "some@mail.com";
    }

    @BeforeEach
    void setUp() {
        setTestingLottery();
        setValidCode();
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

    private void setValidCode() {
        this.validCode = generateValidDtoCode(this.email, this.lottery.getStartDate());
    }

    private void setTestingLottery(){
        Optional<Lottery> optional = this.lotteryDao.findFirstByTitle("Participant Lottery SQL 1");
        if (optional.isPresent()) {
            this.lottery = optional.get();
        } else {
            //            throw new RuntimeException("lottery not found");
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
        String json = "{\n" +
                "    \"email\": \"some@mail.com\",\n" +
                "    \"age\": 21,\n" +
                "    \"code\": \""+ this.validCode +"\",\n" +
                "    \"id\": \""+ this.lottery.getId() +"\"\n" + // id -> meaning lottery_id
                "}";
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
}