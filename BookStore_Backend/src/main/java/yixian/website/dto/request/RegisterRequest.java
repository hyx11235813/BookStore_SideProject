package yixian.website.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import yixian.website.validation.ValidationPatterns;

/**
 * 帳號可以不填，所以可以為null
 */
public record RegisterRequest(
        @Pattern(regexp = ValidationPatterns.ACCOUNT, message = "帳號格式不正確") String account,
        @NotNull(message = "信箱格式不正確")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = "信箱格式不正確") String email,
        @NotNull(message = "密碼格式不正確")
        @Pattern(regexp = ValidationPatterns.PASSWORD, message = "密碼格式不正確") String password,
        @NotNull(message = "名稱格式不正確")
        @Pattern(regexp = ValidationPatterns.NAME, message = "名稱格式不正確") String name) {
}
