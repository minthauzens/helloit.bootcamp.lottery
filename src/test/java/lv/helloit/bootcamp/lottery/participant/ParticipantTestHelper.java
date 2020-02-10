package lv.helloit.bootcamp.lottery.participant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ParticipantTestHelper {
    public static String generateValidDtoCode(String email) {
        return generateValidDtoCode(email, LocalDate.now());
    }

    public static String generateValidDtoCode(String email, LocalDate lotteryStartDate) {
        String date = lotteryStartDate.format(DateTimeFormatter.ofPattern("ddMMYY"));
        String emailLength = String.format("%0,2d", email.length());
        return date + emailLength + getRandom8DigitNumber();
    }

    private static String getRandom8DigitNumber() {
        double min = 10000000;
        double max = 90000000;
        Random random = new Random();
        Double randomNumber = min + random.nextDouble() * max;
        // returns 8 digits without comma
        return String.format("%.0f", randomNumber);
    }
}
