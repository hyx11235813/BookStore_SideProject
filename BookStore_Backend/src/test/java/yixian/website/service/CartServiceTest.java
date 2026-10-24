package yixian.website.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yixian.website.common.ModifyType;
import yixian.website.dto.request.CartItemRequest;
import yixian.website.dto.request.CartQuantityUpdateRequest;
import yixian.website.entity.Book;
import yixian.website.entity.Cart;
import yixian.website.entity.User;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.CartRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    private static final int USER_ID = 1;
    private static final int BOOK_ID = 7;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookService bookService;

    private CartService cartService;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, userService, bookService);
        user = new User();
        user.setId(USER_ID);
        book = new Book();
        book.setId(BOOK_ID);
        book.setQuantity(5);
    }

    @Test
    void addingNewItemIsCappedByInventory() {
        stubLookup(Optional.empty());

        List<String> failed = cartService.addItems(USER_ID, List.of(new CartItemRequest(BOOK_ID, 99)));

        assertTrue(failed.isEmpty());
        verify(cartRepository).save(org.mockito.ArgumentMatchers.argThat(cart -> cart.getQuantity() == 5));
    }

    @Test
    void itemsWithNonPositiveQuantityAreRejected() {
        List<CartItemRequest> items = List.of(new CartItemRequest(BOOK_ID, 0));

        assertThrows(InvalidDataException.class, () -> cartService.addItems(USER_ID, items));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addingToFullCartReportsOutOfStock() {
        Cart existing = cartOf(5);
        stubLookup(Optional.of(existing));

        List<String> failed = cartService.addItems(USER_ID, List.of(new CartItemRequest(BOOK_ID, 1)));

        assertEquals(1, failed.size());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void increasingBeyondInventoryIsRejected() {
        when(cartRepository.findByUserIdAndBookId(USER_ID, BOOK_ID)).thenReturn(Optional.of(cartOf(5)));

        assertThrows(InvalidDataException.class,
                () -> cartService.updateQuantity(USER_ID, new CartQuantityUpdateRequest(BOOK_ID, ModifyType.ADD)));
    }

    @Test
    void decreasingAnEmptyCartRowDeletesIt() {
        Cart existing = cartOf(0);
        when(cartRepository.findByUserIdAndBookId(USER_ID, BOOK_ID)).thenReturn(Optional.of(existing));

        cartService.updateQuantity(USER_ID, new CartQuantityUpdateRequest(BOOK_ID, ModifyType.SUBTRACT));

        verify(cartRepository).delete(existing);
    }

    @Test
    void decreasingReducesQuantityByOne() {
        Cart existing = cartOf(3);
        when(cartRepository.findByUserIdAndBookId(USER_ID, BOOK_ID)).thenReturn(Optional.of(existing));

        cartService.updateQuantity(USER_ID, new CartQuantityUpdateRequest(BOOK_ID, ModifyType.SUBTRACT));

        assertEquals(2, existing.getQuantity());
    }

    private void stubLookup(Optional<Cart> cart) {
        when(userService.findById(USER_ID)).thenReturn(user);
        when(bookService.findById(BOOK_ID)).thenReturn(book);
        when(cartRepository.findByUserIdAndBookId(USER_ID, BOOK_ID)).thenReturn(cart);
    }

    private Cart cartOf(int quantity) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setBook(book);
        cart.setQuantity(quantity);
        return cart;
    }
}
