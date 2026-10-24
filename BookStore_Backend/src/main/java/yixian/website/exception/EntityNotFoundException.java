package yixian.website.exception;

import org.springframework.http.HttpStatus;

/**
 * 404 Not Found：查詢的資源不存在
 */
public class EntityNotFoundException extends ApiException {

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, null, null);
    }
}
