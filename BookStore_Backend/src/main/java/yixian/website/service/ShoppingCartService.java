package yixian.website.service;

import org.springframework.stereotype.Service;
import yixian.website.common.ModifyType;
import yixian.website.common.Result;
import yixian.website.dto.CartDto;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.model.Book;
import yixian.website.model.Cart;
import yixian.website.model.Users;
import yixian.website.repository.ShoppingCartRepository;
import yixian.website.service.query.BookQueryService;
import yixian.website.service.query.UserQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserQueryService userQueryService;
    private final BookQueryService bookQueryService;


    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserQueryService userQueryService, BookQueryService bookQueryService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userQueryService = userQueryService;
        this.bookQueryService = bookQueryService;
    }

    public List<String> addItemsToCart(List<CartDto> cartItems) {
        List<String> failedItems = new ArrayList<>(); //收集庫存不足，加入失敗的資料
        List<Cart> cartsToSave = new ArrayList<>();
        //處理List中的每一本書，加入資料庫
        for (CartDto cartDto : cartItems) {
            Users user = userQueryService.findUserById(cartDto.getUserId());
            Book book = bookQueryService.findBookById(cartDto.getBookId());
            Integer addedQuantity = cartDto.getQuantity();
            int inventory = book.getQuantity();

            // 查詢ShoppingCart中是否存在相同資料，如果有則更新數量；沒有則新增資料
            Optional<Cart> cartOptional = shoppingCartRepository.findByUser_IdAndBook_Id(user.getId(), book.getId());
            Cart cart;

            if (cartOptional.isPresent()) { //如果商品已經被該用戶加入購物車過
                cart = cartOptional.get();
                Integer currentQty = cart.getQuantity(); // 目前用戶已加入的數量
                Integer allowedToAdd = inventory - currentQty;  // 允許加入購物車的數量
                if (allowedToAdd <= 0) {
                    failedItems.add("商品 ID: " + cartDto.getBookId() + " 庫存不足");
                    continue;
                }
                int finalAdd = Math.min(addedQuantity, allowedToAdd); // 最後新增的數量，不能超過庫存
                cart.setQuantity(currentQty + finalAdd);  // 更新購物車數量
            } else {
                // 第一次加入購物車
                cart = new Cart();
                cart.setUser(user);
                cart.setBook(book);
                int finalAdd = Math.min(addedQuantity, inventory);
                cart.setQuantity(finalAdd);
            }
            cartsToSave.add(cart);
        }
        shoppingCartRepository.saveAll(cartsToSave);

        return failedItems;
    }


    public void updateCartQuantity(CartDto dto) {
        ModifyType modifyType = ModifyType.valueOf(dto.getModifyType().toString());
        Cart cart = shoppingCartRepository.findByUser_IdAndBook_Id(dto.getUserId(), dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("找不到USER_ID為 " + dto.getUserId() + " Book_ID為 " + dto.getBookId() + " 的資料"));
        processCartQuantity(cart, modifyType);
    }


    private void processCartQuantity(Cart cart, ModifyType modifyType) {
        Integer bookInventory = cart.getBook().getQuantity();
        switch (modifyType) {
            case ADD -> {
                if (cart.getQuantity() >= bookInventory) {
                    throw new InvalidDataException("超過庫存限制");
                }
                cart.setQuantity(cart.getQuantity() + 1);
                shoppingCartRepository.save(cart);
            }
            case SUBTRACT -> {
                if (cart.getQuantity() <= 0) {
                    shoppingCartRepository.deleteById(cart.getId()); //小於零直接刪除該筆資料
                } else {
                    cart.setQuantity(cart.getQuantity() - 1);
                    shoppingCartRepository.save(cart);
                }

            }
            default -> throw new InvalidDataException("無效的 modifyType: " + modifyType);

        }

    }
}
