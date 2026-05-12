package yixian.website.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import yixian.website.common.ModifyType;

@Getter
@Setter
public class CartDto {

    @NotNull(message = "userId不得為空")
    private Integer userId;
    @NotNull(message = "bookId不得為空")
    private Integer bookId;
    @NotNull(message = "quantity不得為空")
    private Integer quantity;

    private ModifyType modifyType;
}
