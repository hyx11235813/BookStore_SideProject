package yixian.website.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)//能夠用@CreatedDate 和 @LastModifiedDate 等註解，自動記錄時間
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "isbn")
    private String isbn;
    @Column(name = "book_name")
    private String bookName;
    @Column(name = "author")
    private String author;
    @Column(name = "category")
    private String category;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "published_date")
    private String publishedDate;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "sales")
    private Integer sales;
    @Column(name = "price")
    private Integer price;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "book_cover_url")
    private String bookCoverUrl;
    @Lob
    @Column(name = "book_cover_byte")
    private byte[] bookCoverByte;
    @CreatedDate
    @Column(name = "creation_date")
    private Date creationDate;
    @LastModifiedDate
    @Column(name = "edit_date")
    private Date editDate;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<BookSale> bookSales;



}
