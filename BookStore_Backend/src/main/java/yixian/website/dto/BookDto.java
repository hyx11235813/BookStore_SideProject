package yixian.website.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class BookDto {
    private Integer id;
    private String isbn;
    private String bookName;
    private String author;
    private String category;
    private String publisher;
    private String publishedDate;
    private Integer quantity;
    private Integer orderQuantity;
    private Integer price;
    private String description;
    private String api_bookCoverUrl = "";
    private byte[] bookCoverByte;
    private MultipartFile uploadBookCover;


    public BookDto(Integer id, String bookName, String author, byte[] bookCoverByte, Integer price, Integer quantity, Integer orderQuantity) {
        this.id = id;
        this.bookName = bookName;
        this.author = author;
        this.bookCoverByte = bookCoverByte;
        this.price = price;
        this.quantity = quantity;
        this.orderQuantity = orderQuantity;
    }
}
