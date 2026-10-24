package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyResetTokenRequest(
        @NotBlank(message = "Email不得為空") String email,
        @NotBlank(message = "Token不得為空") String token) {
}
