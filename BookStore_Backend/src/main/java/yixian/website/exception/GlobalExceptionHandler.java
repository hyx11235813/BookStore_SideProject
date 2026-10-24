package yixian.website.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import yixian.website.common.ErrorMessages;
import yixian.website.common.Result;

/**
 * 全局異常處理，捕獲拋出的異常，並返回統一格式的結果
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String INVALID_REQUEST = "請求參數錯誤";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Result<Object>> handleApiException(ApiException e) {
        if (e.getStatus().is5xxServerError()) {
            log.error(e.getMessage(), e);
        }
        return respond(e.getStatus(), e.getData(), e.getMessage());
    }

    //Bean Validation 驗證 @RequestBody / @ModelAttribute 失敗
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Object>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse(INVALID_REQUEST);
        return respond(HttpStatus.BAD_REQUEST, null, message);
    }

    //Bean Validation 驗證 controller 方法參數失敗
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Result<Object>> handleMethodValidation(HandlerMethodValidationException e) {
        String message = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(INVALID_REQUEST);
        return respond(HttpStatus.BAD_REQUEST, null, message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Result<Object>> handleUnreadableRequest(Exception e) {
        return respond(HttpStatus.BAD_REQUEST, null, INVALID_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleUnexpected(Exception e) {
        //Spring MVC 自己拋出的例外（404、405、缺少參數…）本身就帶有正確的 HTTP status
        if (e instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
            return respond(status, null, status.getReasonPhrase());
        }
        log.error("Unexpected error", e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, null, ErrorMessages.SERVER_ERROR);
    }

    private ResponseEntity<Result<Object>> respond(HttpStatus status, Object data, String message) {
        return ResponseEntity.status(status).body(Result.fail(status, data, message));
    }
}
