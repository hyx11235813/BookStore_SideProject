package yixian.website.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class Result<T> {
    private T data;
    private Integer code;
    private String message;

    public Result(Integer code, T data, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    private static <T> Result<T> success(Integer code, T data, String message) {
        return new Result<>(code, data, message);
    }

    private static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, null, message);
    }

    //Success
    public static Result saveSuccess(Object data, String message) {
        return success(HttpStatus.OK.value(), data, message);
    }

    public static Result getSuccess(Object data, String message) {
        return success(HttpStatus.OK.value(), data, message);
    }

    public static Result updateSuccess(Object data, String message) {
        return success(HttpStatus.OK.value(), data, message);
    }

    public static Result loginSuccess(Object data) {
        return success(200, data, "登入成功");
    }

    //Error
    public static Result saveError(String message) {
        return error(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static Result updateError(String message) {
        return error(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static Result getError(String message) {
        return error(HttpStatus.BAD_REQUEST.value(), message);
    }
    public static Result loginError(Integer code,String message) {
        return error(code,  message);
    }

    //Exception
    public static Result exceptionError(Integer code, Object data, String message) {
        return new Result(code, data, message);

    }
}
