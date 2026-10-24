package yixian.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yixian.website.dto.response.CartItemResponse;
import yixian.website.entity.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserIdAndBookId(Integer userId, Integer bookId);

    @Query("SELECT new yixian.website.dto.response.CartItemResponse(b.id, b.bookName, b.author, b.bookCoverByte, b.price, b.quantity, c.quantity) " +
            "FROM Cart c JOIN c.book b WHERE c.user.id = :userId")
    List<CartItemResponse> findCartItemsByUserId(@Param("userId") Integer userId);
}
