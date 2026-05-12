package yixian.website.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookSearchRequestDto {
    private List<Integer> ids;
    private String cat = "";
    private String q = ""; //query
    private String sortBy = "book_name";
    private Integer currentPage = 1;
    private Integer pageSize = 10;
}
