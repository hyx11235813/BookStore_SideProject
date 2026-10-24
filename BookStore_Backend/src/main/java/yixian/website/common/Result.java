package yixian.website.common;

import org.springframework.http.HttpStatus;

/**
 * 所有 API 統一的回應格式。
 * code 與 HTTP status 一致，前端可直接依 code 判斷處理結果。
 */
public record Result<T>(Integer code, T data, String message) {

    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(HttpStatus.OK.value(), data, message);
    }

    public static Result<Void> ok(String message) {
        return ok(null, message);
    }

    public static <T> Result<T> fail(HttpStatus status, T data, String message) {
        return new Result<>(status.value(), data, message);
    }

    public static Result<Void> fail(HttpStatus status, String message) {
        return fail(status, null, message);
    }
}
