package yixian.website.exception;

import org.springframework.http.HttpStatus;

/**
 * 400 Bad Request：請求有錯誤（例如資料格式或內容不正確）
 */
public class InvalidDataException extends ApiException {

    public InvalidDataException(String message) {
        this(message, null);
    }

    public InvalidDataException(String message, Object data) {
        super(HttpStatus.BAD_REQUEST, message, data, null);
    }
}
