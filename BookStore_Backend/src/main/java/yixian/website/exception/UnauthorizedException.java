package yixian.website.exception;

import org.springframework.http.HttpStatus;

/**
 * 401 Unauthorized：缺少有效的身份驗證憑證
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message, null, null);
    }
}
