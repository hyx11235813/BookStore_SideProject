package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordCheckRequest(@NotBlank(message = "密碼輸入不得為空") String password) {
}
