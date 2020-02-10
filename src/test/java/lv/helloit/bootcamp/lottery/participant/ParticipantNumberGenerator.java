package lv.helloit.bootcamp.lottery.participant;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class ParticipantNumberGenerator {
    public static String generateValidDtoCode(String email) {
        return generateValidDtoCode(email, LocalDate.now());
    }

    public static String generateValidDtoCode(String email, LocalDate lotteryStartDate) {
        return generateValidDtoCodeFirstHalf(email, lotteryStartDate) + getRandom8DigitNumber();
    }

    public static String generateValidDtoCodeFirstHalf(String email, LocalDate lotteryStartDate) {
        if (email.length() > 99) {
            throw new RuntimeException("valid code only can be generated for emails with length less than 100");
        }

        String date = lotteryStartDate.format(DateTimeFormatter.ofPattern("ddMMYY"));
        String emailLength = String.format("%0,2d", email.length());
        return date + emailLength;

    }

    public static String longTo8digitString(long number) {
        NumberFormat numberFormat =
                NumberFormat.getInstance(new Locale("en", "US"));
        numberFormat.setMinimumIntegerDigits(8);
        numberFormat.setMaximumIntegerDigits(8);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setGroupingUsed(false);
        return numberFormat.format(number);
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
