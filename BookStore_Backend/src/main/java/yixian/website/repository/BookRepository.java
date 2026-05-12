package yixian.website.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yixian.website.model.Book;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository

public interface BookRepository extends JpaRepository<Book, Integer> {

    //計算某日期之後新增的數量
    long countByCreationDateAfter(Date date);

    //計算某日期之内新增的書籍數量
    long countByCreationDateBetween(Date startOfMonth, Date endOfMonth);

    Page<Book> findByIsDeletedFalse(Pageable pageable);

    Page<Book> findByIsDeletedTrue(Pageable pageable);


    Optional<Book> findBookByIsbn(String isbn);

    //根據category獲取相同類型,且銷量最好的前10名書籍
    List<Book> findTop10ByCategoryOrderBySalesDesc(String category);


    //找出前10銷售量最高的書籍
    List<Book> findTop10ByOrderBySalesDesc();

    //找出前10名新上架的書籍
    List<Book> findTop10ByOrderByCreationDateDesc();

    @Query(value = "SELECT * FROM Book WHERE 1=1 " +
            "AND (:cat IS NULL OR category LIKE CONCAT('%', :cat, '%')) " +
            "AND (:query IS NULL OR :query = '' OR (author LIKE CONCAT('%', :query, '%') " +
            "OR book_name LIKE CONCAT('%', :query, '%') " +
            "OR isbn LIKE CONCAT(:query, '%') " +
            "OR publisher LIKE CONCAT('%', :query, '%')))",
            nativeQuery = true)
    Page<Book> findTop100ByParams(@Param("cat") String cat, @Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM Book WHERE 1=1 " +
            "AND (:cat IS NULL OR category LIKE CONCAT('%', :cat, '%')) " +
            "AND (:query IS NULL OR :query = '' OR (author LIKE CONCAT('%', :query, '%') " +
            "OR book_name LIKE CONCAT('%', :query, '%') " +
            "OR isbn LIKE CONCAT(:query, '%') " +
            "OR publisher LIKE CONCAT('%', :query, '%')))",
            nativeQuery = true)
    Page<Book> findBooksByParams(@Param("cat") String cat, @Param("query") String query, Pageable pageable);



    @Query(value = "SELECT * FROM book WHERE 1=1 " +
            "AND (:isbn IS NULL OR isbn = :isbn) " +
            "AND (:bookName IS NULL OR book_name LIKE CONCAT('%', :bookName, '%')) " +
            "AND (:author IS NULL OR author LIKE CONCAT('%', :author, '%')) " +
            "AND (:publisher IS NULL OR publisher LIKE CONCAT('%', :publisher, '%')) " +
            "AND (:category IS NULL OR category = :category) " +
            "AND (:creationDateStart IS NULL OR creation_date >= :creationDateStart) " +
            "AND (:creationDateEnd IS NULL OR creation_date < DATEADD(day,1,:creationDateEnd)) " +//使用DateAdd(day,1,:creationDateEnd)是爲了讓查詢的結束日期包含完整的一天
            "AND (:editDateStart IS NULL OR edit_date >= :editDateStart) " +
            "AND (:editDateEnd IS NULL OR edit_date < DATEADD(day,1,:editDateEnd)) " +
            "AND (:isDeleted IS NULL OR is_deleted = :isDeleted)",
            nativeQuery = true)
    Page<Book> searchBooksByParams(@Param("isbn") String isbn,
                                   @Param("bookName") String bookName,
                                   @Param("author") String author,
                                   @Param("publisher") String publisher,
                                   @Param("category") String category,
                                   @Param("creationDateStart") String creationDateStart,
                                   @Param("creationDateEnd") String creationDateEnd,
                                   @Param("editDateStart") String editDateStart,
                                   @Param("editDateEnd") String editDateEnd,
                                   @Param("isDeleted") Boolean isDeleted,
                                   Pageable pageable);

}
