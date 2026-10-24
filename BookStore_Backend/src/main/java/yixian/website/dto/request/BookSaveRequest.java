package yixian.website.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理員新增或編輯書籍（multipart/form-data）。id 為 null 代表新增。
 * 封面圖片二擇一：使用者上傳的檔案，或由外部 API 提供的圖片網址。
 */
@Getter
@Setter
public class BookSaveRequest {
    private Integer id;
    private String isbn;
    private String bookName;
    private String author;
    private String category;
    private String publisher;
    private String publishedDate;
    @PositiveOrZero(message = "庫存不得為負數")
    private Integer quantity;
    @PositiveOrZero(message = "價格不得為負數")
    private Integer price;
    private String description;
    private String coverUrl;
    private MultipartFile uploadBookCover;
}
