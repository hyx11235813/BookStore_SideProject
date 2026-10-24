package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import yixian.website.validation.ValidationPatterns;

public record ResetPasswordRequest(
        @NotBlank(message = "Email不得為空") String email,
        @NotBlank(message = "驗證碼不得為空") String token,
        @NotBlank(message = "新密碼不得為空")
        @Pattern(regexp = ValidationPatterns.PASSWORD, message = "密碼格式錯誤") String password) {
}
