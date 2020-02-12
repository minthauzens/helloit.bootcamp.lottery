package lv.helloit.bootcamp.lottery.lottery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {
    LotteryService victim;

    @Mock
    private LotteryDao lotteryDao;
    @Mock LotteryWithParticipantCountDao lotteryWithParticipantCountDao;

    @BeforeEach
    void setUp() {
        this.victim = new LotteryService(lotteryDao, lotteryWithParticipantCountDao);
    }


}