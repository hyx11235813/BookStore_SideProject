package yixian.website.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final Integer code;

    public EntityNotFoundException(String message) {
        super(message);
        this.code = 404;
    }
}
