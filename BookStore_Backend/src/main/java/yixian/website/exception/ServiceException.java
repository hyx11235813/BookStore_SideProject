package yixian.website.exception;

import org.springframework.http.HttpStatus;

/**
 * 500 Internal Server Error：伺服器端處理失敗
 */
public class ServiceException extends ApiException {

    public ServiceException(String message) {
        this(message, null);
    }

    public ServiceException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, null, cause);
    }
}
