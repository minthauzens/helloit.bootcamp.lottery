package lv.helloit.bootcamp.lottery.lottery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void shouldReturnAllLotteries() throws Exception {
        String jsonContent = "{\n" +
                "  \"title\": \"Injection Lottery 2\",\n" +
                "  \"limit\": 12345\n" +
                "}";
        registerLottery(jsonContent);

        ArrayList<Lottery> lotteries = getAllLotteries();

        assertFalse(lotteries.isEmpty());
        assertThat(lotteries, containsInAnyOrder(
                hasProperty("title", is("Injection Lottery 2"))
        ));
        assertThat(lotteries, containsInAnyOrder(
                hasProperty("limit", is(12345))
        ));

    }

    private ArrayList<Lottery> getAllLotteries() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/status"))
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
            public int compareTo(TypeReference<ArrayList<Lottery>> o) {
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
        performFaultyLotteryRegistration("  \"title\": \"Injection Lottery Fail\"\n");
        performFaultyLotteryRegistration("  \"limit\": 1000\n");
        int sizeAfter = getAllLotteries().size();

        assertEquals(sizeBefore, sizeAfter);
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
                .andExpect(jsonPath("$.status").value("Fail"))
                .andReturn();
    }
}