package lv.helloit.bootcamp.lottery.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityBuilder {

    public static ResponseEntity<String> createResponseEntityFail(String reason) {
        reason = "\"reason\":  \"" + reason + "\"\n";
        return createResponseEntity("Fail", HttpStatus.BAD_REQUEST, reason);
    }

    public static ResponseEntity<String> createResponseEntityOkWithWinnerCode(String winnerCode) {
        winnerCode = "\"winnerCode\":  \"" + winnerCode + "\"\n";
        return createResponseEntity("OK", HttpStatus.OK, winnerCode);
    }

    public static ResponseEntity<String> createResponseEntityOkWithId(Long id) {
        String idString = "\"id\":  \"" + id + "\"\n";
        return createResponseEntity("OK", HttpStatus.OK, idString);
    }

    public static ResponseEntity<String> createResponseEntityOkWithId(Long id, HttpStatus status) {
        String idString = "\"id\":  \"" + id + "\"\n";
        return createResponseEntity("OK", status, idString);
    }

    public static ResponseEntity<String> createResponseEntity(String status, HttpStatus httpStatus, String content) {
        String json = "{\n" +
                "\"status\":  \"" + status + "\",\n" +
                content +
                "}";
        return new ResponseEntity<>(json, httpStatus);
    }

    public static ResponseEntity<String> createResponseEntityOk() {
        return createResponseEntity("OK", HttpStatus.OK, null);
    }
}
