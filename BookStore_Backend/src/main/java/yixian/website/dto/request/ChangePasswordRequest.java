package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import yixian.website.validation.ValidationPatterns;

public record ChangePasswordRequest(
        @NotBlank(message = "密碼格式錯誤")
        @Pattern(regexp = ValidationPatterns.PASSWORD, message = "密碼格式錯誤") String password) {
}
