package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateAvatarRequest(@NotBlank(message = "頭像路徑不能為空") String avatar) {
}
