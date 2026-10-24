package yixian.website.exception;

import org.springframework.http.HttpStatus;

/**
 * 403 Forbidden：已通過驗證，但沒有訪問該資源的權限
 */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message, null, null);
    }
}
