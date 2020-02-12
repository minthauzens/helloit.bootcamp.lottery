package lv.helloit.bootcamp.lottery.utils;

public class ValidatorResponse {
    private boolean status;
    private String message;

    public ValidatorResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ValidatorResponse() {
        this.status = true;
    }

    public ValidatorResponse(String message) {
        this.status = false;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean hasErrors() {
        return !status;
    }

    public String getMessage() {
        return message;
    }

    public void setStatusFalseWithMessage(String message) {
        this.status = false;
        this.message = message;
    }
}
