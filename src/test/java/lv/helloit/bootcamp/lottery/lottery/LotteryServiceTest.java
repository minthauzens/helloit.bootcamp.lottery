package lv.helloit.bootcamp.lottery.lottery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {
    LotteryService victim;

    @Mock
    private LotteryDao lotteryDao;

    @BeforeEach
    void setUp() {
        this.victim = new LotteryService(lotteryDao);
    }


}