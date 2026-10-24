package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(@NotBlank(message = "Email不得為空") String email) {
}
