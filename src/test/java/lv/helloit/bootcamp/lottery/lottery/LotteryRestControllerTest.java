package lv.helloit.bootcamp.lottery.lottery;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/schema-test.sql")
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
                .andReturn();

    }
    @Test
    void shouldReturnAllLotteries() throws Exception {
        String jsonContent = "{\n" +
                "  \"title\": \"Injection Lottery 2\",\n" +
                "  \"limit\": 1000\n" +
                "}";
        registerLottery(jsonContent);

        MvcResult mvcResult = mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
//                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        HashMap<String, Lottery> lotteries = objectMapper.readValue(json, LotteryMap.class);
        for (Lottery lottery : lotteries.values()) {
            System.out.println(lottery);
        }


    }
    @NoArgsConstructor
    private class LotteryMap extends HashMap<String, Lottery> {
    }

    private ResultActions registerLottery(String jsonContent) throws Exception {
        return mockMvc.perform(post("/start-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnHttpStatusBadRequestWhenNoTitleOrLimitForLotteryRegistration() throws Exception {
        performFaultyLotteryRegistration("  \"title\": \"Injection Lottery Fail\"\n");
        performFaultyLotteryRegistration("  \"limit\": 1000\n");
    }

    private void performFaultyLotteryRegistration(String jsonContent) throws Exception {
        mockMvc.perform(post("/start-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        jsonContent +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.reason")
                        .value("Please provide valid lottery properties. Title has to be provided and limit has to be numeric and larger than 1"))
                .andReturn();
    }
}