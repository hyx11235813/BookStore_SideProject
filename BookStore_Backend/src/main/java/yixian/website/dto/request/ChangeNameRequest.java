package yixian.website.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeNameRequest(@NotBlank(message = "用戶名稱不能為空") String name) {
}
