package lv.helloit.bootcamp.lottery.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class Response {
    boolean status;
    String message;
    HttpStatus httpStatus;

    public boolean hasErrors() {
        return !this.status;
    }
}
