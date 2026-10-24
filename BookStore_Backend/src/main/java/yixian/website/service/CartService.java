package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.dto.request.CartItemRequest;
import yixian.website.dto.request.CartQuantityUpdateRequest;
import yixian.website.dto.response.CartItemResponse;
import yixian.website.entity.Book;
import yixian.website.entity.Cart;
import yixian.website.entity.User;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserService userService;
    private final BookService bookService;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(Integer userId) {
        return cartRepository.findCartItemsByUserId(userId);
    }

    /**
     * 把商品加入購物車，數量不會超過庫存。
     *
     * @return 因為庫存不足而加入失敗的訊息
     */
    @Transactional
    public List<String> addItems(Integer userId, List<CartItemRequest> items) {
        items.forEach(this::validate);
        User user = userService.findById(userId);
        List<String> failedItems = new ArrayList<>();

        for (CartItemRequest item : items) {
            Book book = bookService.findById(item.bookId());
            Cart cart = cartRepository.findByUserIdAndBookId(userId, book.getId())
                    .orElseGet(() -> newCart(user, book));

            int inventory = book.getQuantity() == null ? 0 : book.getQuantity();
            int allowedToAdd = inventory - cart.getQuantity(); // 允許加入購物車的數量
            if (allowedToAdd <= 0) {
                failedItems.add("商品 ID: " + item.bookId() + " 庫存不足");
                continue;
            }
            cart.setQuantity(cart.getQuantity() + Math.min(item.quantity(), allowedToAdd));
            cartRepository.save(cart);
        }
        return failedItems;
    }

    @Transactional
    public void updateQuantity(Integer userId, CartQuantityUpdateRequest request) {
        Cart cart = cartRepository.findByUserIdAndBookId(userId, request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("找不到USER_ID為 " + userId + " Book_ID為 " + request.bookId() + " 的資料"));

        switch (request.modifyType()) {
            case ADD -> increase(cart);
            case SUBTRACT -> decrease(cart);
        }
    }

    private void validate(CartItemRequest item) {
        if (item.bookId() == null) {
            throw new InvalidDataException("bookId不得為空");
        }
        if (item.quantity() == null || item.quantity() <= 0) {
            throw new InvalidDataException("quantity必須大於0");
        }
    }

    private Cart newCart(User user, Book book) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setBook(book);
        cart.setQuantity(0);
        return cart;
    }

    private void increase(Cart cart) {
        Integer inventory = cart.getBook().getQuantity();
        if (inventory == null || cart.getQuantity() >= inventory) {
            throw new InvalidDataException("超過庫存限制");
        }
        cart.setQuantity(cart.getQuantity() + 1);
    }

    //前端把數量減到 0 後，再按一次才會移除商品，所以只有數量已經是 0 時才刪除該筆資料
    private void decrease(Cart cart) {
        if (cart.getQuantity() <= 0) {
            cartRepository.delete(cart);
        } else {
            cart.setQuantity(cart.getQuantity() - 1);
        }
    }
}
