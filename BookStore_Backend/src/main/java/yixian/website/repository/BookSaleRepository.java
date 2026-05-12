package yixian.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yixian.website.model.BookSale;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface BookSaleRepository extends JpaRepository<BookSale, Integer> {
    long countBySaleDateAfter(Date date);

    long countBySaleDateBetween(Date startOfMonth, Date endOfMonth);


    @Query("SELECT bs.book.category,count(bs.id) FROM BookSale bs GROUP BY bs.book.category")
    List<Object[]> countBookSaleByCategory();

    @Query("SELECT SUM(bs.price) FROM BookSale bs WHERE bs.saleDate BETWEEN :startOfMonth AND :endOfMonth")
    BigDecimal sumTotalSalePriceBetweenMonths(Date startOfMonth, Date endOfMonth);


}
