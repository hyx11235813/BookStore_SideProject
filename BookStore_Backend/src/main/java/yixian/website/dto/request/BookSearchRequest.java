package yixian.website.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 管理端的書籍搜尋條件
 */
@Getter
@Setter
public class BookSearchRequest {
    private String isbn;
    private String bookName;
    private String author;
    private String publisher;
    private String category;
    private String creationDateStart;
    private String creationDateEnd;
    private String editDateStart;
    private String editDateEnd;
    private boolean deleted;
}
