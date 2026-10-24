package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "帳號或信箱不得為空") String loginIdentifier,
        @NotBlank(message = "密碼輸入不得為空") String password) {
}
