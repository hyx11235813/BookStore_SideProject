package yixian.website.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final Integer code;

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }


    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
