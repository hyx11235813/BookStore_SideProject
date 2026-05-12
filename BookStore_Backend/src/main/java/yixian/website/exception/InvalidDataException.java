package yixian.website.exception;

import lombok.Getter;

@Getter
public class InvalidDataException extends RuntimeException {
    private final Integer code;
    private Object data;

    public InvalidDataException(String message) {
        super(message);
        this.code = 400;
    }


    public InvalidDataException(String message, Object data) {
        super(message);
        this.code = 400;
        this.data = data;
    }
}
