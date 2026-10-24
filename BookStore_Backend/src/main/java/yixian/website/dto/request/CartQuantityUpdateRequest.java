package yixian.website.dto.request;

import jakarta.validation.constraints.NotNull;
import yixian.website.common.ModifyType;

public record CartQuantityUpdateRequest(
        @NotNull(message = "bookId不得為空") Integer bookId,
        @NotNull(message = "modifyType不得為空") ModifyType modifyType) {
}
