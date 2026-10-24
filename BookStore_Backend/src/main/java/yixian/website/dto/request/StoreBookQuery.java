package yixian.website.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 使用者端的書籍搜尋條件（分類、關鍵字、排序，加上分頁）
 */
@Getter
@Setter
public class StoreBookQuery extends PageParams {
    private String cat = "";
    private String q = ""; //query
    private String sortBy = "book_name";
}
