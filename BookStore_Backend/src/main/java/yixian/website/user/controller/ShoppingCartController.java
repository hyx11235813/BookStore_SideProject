package yixian.website.user.controller;

import org.springframework.web.bind.annotation.*;
import yixian.website.common.ErrorMessages;
import yixian.website.common.Result;
import yixian.website.dto.BookDto;
import yixian.website.dto.CartDto;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.ShoppingCartRepository;
import yixian.website.service.ShoppingCartService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/user")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartRepository shoppingCartRepository;


    public ShoppingCartController(ShoppingCartService shoppingCartService, ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartService = shoppingCartService;
        this.shoppingCartRepository = shoppingCartRepository;

    }


    @PostMapping("/cart")
    public Result addToCart(@RequestBody List<CartDto> cartItems) {
        List<String> failedItems = shoppingCartService.addItemsToCart(cartItems);

        if (!failedItems.isEmpty()) {
            return Result.saveSuccess(failedItems, "庫存不足");
        }
        return Result.saveSuccess(null, "購物車更新成功");
    }


    @GetMapping("/cart/{userId}")
    public Result getUserCartInfo(@PathVariable Integer userId) {
        if (userId <= 0) {
            throw new InvalidDataException("USERID：" + userId + "錯誤");
        }
        List<BookDto> userCart = shoppingCartRepository.findUserCartByUserId(userId);
        if (userCart == null || userCart.isEmpty()) {
            return Result.getSuccess(new ArrayList<>(), "用戶購物車目前沒有商品");
        }
        return Result.getSuccess(userCart, "獲取用戶購物車資料成功");
    }


    @PostMapping("/cart/update-quantity")
    public Result updateCartQuantity(@RequestBody CartDto dto) {
        shoppingCartService.updateCartQuantity(dto);
        switch (dto.getModifyType()) {
            case ADD -> {
                return Result.updateSuccess(null, "更新成功");
            }
            case SUBTRACT -> {
                return Result.updateSuccess(null, "刪除成功");
            }
            default -> {
            }
        }
        return Result.updateSuccess(null, "更新成功");
    }
}

