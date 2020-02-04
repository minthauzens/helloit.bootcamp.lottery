package lv.helloit.bootcamp.lottery.lottery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LotteryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterLottery() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/start-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"title\": \"Injection Lottery 1\",\n" +
                        "  \"limit\": 1000\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.title").value("Injection Lottery"))
                .andExpect(jsonPath("$.limit").value(1000))
                .andExpect(jsonPath("$.startDate").value(LocalDate.now()))
                .andReturn();

    }
}