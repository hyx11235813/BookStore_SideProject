package yixian.website.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 可預期的業務例外，由 {@link GlobalExceptionHandler} 轉成統一的回應格式。
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final transient Object data;

    protected ApiException(HttpStatus status, String message, Object data, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.data = data;
    }
}
