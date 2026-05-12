package yixian.website.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yixian.website.dto.BookDto;
import yixian.website.model.Cart;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUser_IdAndBook_Id(Integer userId, Integer id);

    @Query("SELECT new yixian.website.dto.BookDto ( b.id,b.bookName,b.author,b.bookCoverByte ,b.price,b.quantity,sc.quantity)" +
            "FROM Cart sc JOIN sc.book b WHERE sc.user.id=:userId")
    List<BookDto> findUserCartByUserId(@Param("userId") Integer userId);




}
