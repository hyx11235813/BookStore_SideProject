package yixian.website.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.ModifyType;
import yixian.website.common.Result;
import yixian.website.dto.request.CartItemRequest;
import yixian.website.dto.request.CartQuantityUpdateRequest;
import yixian.website.dto.response.CartItemResponse;
import yixian.website.security.AuthenticatedUser;
import yixian.website.security.CurrentUser;
import yixian.website.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public Result<List<String>> addToCart(@CurrentUser AuthenticatedUser currentUser,
                                          @RequestBody List<CartItemRequest> items) {
        List<String> failedItems = cartService.addItems(currentUser.id(), items);
        if (!failedItems.isEmpty()) {
            return Result.ok(failedItems, "庫存不足");
        }
        return Result.ok(null, "購物車更新成功");
    }

    @GetMapping("/{userId}")
    public Result<List<CartItemResponse>> getCart(@CurrentUser AuthenticatedUser currentUser,
                                                  @PathVariable Integer userId) {
        currentUser.requireSameUser(userId);
        List<CartItemResponse> items = cartService.getCartItems(userId);
        return Result.ok(items, items.isEmpty() ? "用戶購物車目前沒有商品" : "獲取用戶購物車資料成功");
    }

    @PostMapping("/update-quantity")
    public Result<Void> updateQuantity(@CurrentUser AuthenticatedUser currentUser,
                                       @RequestBody @Valid CartQuantityUpdateRequest request) {
        cartService.updateQuantity(currentUser.id(), request);
        return Result.ok(request.modifyType() == ModifyType.SUBTRACT ? "刪除成功" : "更新成功");
    }
}
