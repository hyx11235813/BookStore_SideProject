package yixian.website.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class SearchBookRequestDto {
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
