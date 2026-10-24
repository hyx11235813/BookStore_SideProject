package yixian.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yixian.website.dto.response.CategorySalesResponse;
import yixian.website.entity.BookSale;

import java.util.Date;
import java.util.List;

public interface BookSaleRepository extends JpaRepository<BookSale, Integer> {

    long countBySaleDateGreaterThanEqual(Date from);

    @Query("SELECT COUNT(bs) FROM BookSale bs WHERE bs.saleDate >= :from AND bs.saleDate < :to")
    long countSoldBetween(@Param("from") Date from, @Param("to") Date to);

    @Query("SELECT COALESCE(SUM(bs.price), 0L) FROM BookSale bs WHERE bs.saleDate >= :from AND bs.saleDate < :to")
    long sumSalePriceBetween(@Param("from") Date from, @Param("to") Date to);

    @Query("SELECT new yixian.website.dto.response.CategorySalesResponse(b.category, COUNT(bs)) " +
            "FROM BookSale bs JOIN bs.book b GROUP BY b.category")
    List<CategorySalesResponse> countSalesByCategory();
}
