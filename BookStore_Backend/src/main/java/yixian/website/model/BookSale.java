package yixian.website.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "book_sale")
@Getter
@Setter
public class BookSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "sale_date")
    private Date saleDate;
    @Column(name = "price")
    private Integer price;
    @ManyToOne
    private Book book;
    @ManyToOne
    private Users user;
}
