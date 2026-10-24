package yixian.website.dto.response;

import java.util.Date;

public record BookResponse(
        Integer id,
        String isbn,
        String bookName,
        String author,
        String category,
        String publisher,
        String publishedDate,
        Integer quantity,
        Integer sales,
        Integer price,
        String description,
        String bookCoverUrl,
        byte[] bookCoverByte,
        Date creationDate,
        Date editDate,
        Boolean isDeleted) {
}
