package lv.helloit.bootcamp.lottery.lottery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test-sqls/schema-base.sql")
public class LotteryRestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterLottery() throws Exception {
        mockMvc.perform(post("/start-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"title\": \"Injection Lottery 1\",\n" +
                        "  \"limit\": 1000\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();

    }

    @Test
    void shouldNotRegisterLotteryIfTitleRepeats() throws Exception {
        String jsonContent = "{\n" +
                "  \"title\": \"Injection Lottery 2\",\n" +
                "  \"limit\": 12345\n" +
                "}";

        registerLottery(jsonContent)
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();

        registerLottery(jsonContent)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery with such title already exists"))
                .andReturn();
    }

    @Test
    void shouldReturnAllLotteries() throws Exception {
        String jsonContent = "{\n" +
                "  \"title\": \"Injection Lottery 3\",\n" +
                "  \"limit\": 12345\n" +
                "}";
        registerLottery(jsonContent);

        ArrayList<LotteryWithParticipantCountDto> lotteries = getAllLotteries();

        assertFalse(lotteries.isEmpty());
        assertThat(lotteries, containsInAnyOrder(
                hasProperty("title", is("Injection Lottery 3"))
        ));
    }

    private ArrayList<LotteryWithParticipantCountDto> getAllLotteries() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/stats"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(json, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }

            @Override
            public int compareTo(TypeReference<ArrayList<LotteryWithParticipantCountDto>> o) {
                return super.compareTo(o);
            }
        });
    }

    private ResultActions registerLottery(String jsonContent) throws Exception {
        return mockMvc.perform(post("/start-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenNoTitleOrLimitForLotteryRegistrationAndNotSaveThemInDB() throws Exception {
        int sizeBefore = getAllLotteries().size();
        performFaultyLotteryRegistration("  \"title\": \"Injection Lottery Fail\"\n")
                .andExpect(jsonPath("$.reason").exists())
                .andReturn();
        performFaultyLotteryRegistration("  \"limit\": 1000\n")
                .andExpect(jsonPath("$.reason").value("title cant be blank"))
                .andReturn();
        int sizeAfter = getAllLotteries().size();

        assertEquals(sizeBefore, sizeAfter);
    }

    private ResultActions performFaultyLotteryRegistration(String jsonContent) throws Exception {
        return mockMvc.perform(post("/start-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        jsonContent +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.status").value("Fail"));
    }

    @Test
    void shouldStopRegistration() throws Exception {
        long lottery_id = registerLotteryAndGetItsId("Injection Lottery 4");

        stopRegistration(lottery_id)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();
    }

    private long registerLotteryAndGetItsId(String title) throws Exception {
        String jsonContent = "{\n" +
                "  \"title\": \"" + title + "\",\n" +
                "  \"limit\": 2\n" +
                "}";

        MvcResult mvcResult = registerLottery(jsonContent)
                .andReturn();

        String jsonString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.getLong("id");
    }

    @Test
    void shouldFailIfSuchIdDoesntExist() throws Exception {
        stopRegistration(9999999)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery with Id 9999999 doesn't exist"))
                .andReturn();
    }

    @Test
    void shouldFailIfLotteryWasAlreadyStopped() throws Exception {
        long lottery_id = registerLotteryAndGetItsId("Injection Lottery 5");

        stopRegistration(lottery_id)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andReturn();

        stopRegistration(lottery_id)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("Fail"))
                .andExpect(jsonPath("$.reason").value("Lottery registration was already stopped at " + LocalDate.now()))
                .andReturn();
    }

    private ResultActions stopRegistration(long lottery_id) throws Exception {
        return mockMvc.perform(post("/stop-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"id\": \"" + lottery_id + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON));
    }


}