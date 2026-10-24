package yixian.website.mapper;

import yixian.website.dto.response.BookResponse;
import yixian.website.entity.Book;
import yixian.website.util.ImageUtils;

public final class BookMapper {

    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
        return toResponse(book, book.getBookCoverUrl());
    }

    /**
     * 詳細資料額外把封面轉成 data URL，前端可以直接放進 img 的 src 顯示
     */
    public static BookResponse toDetailResponse(Book book) {
        return toResponse(book, ImageUtils.toDataUrl(book.getBookCoverByte()));
    }

    private static BookResponse toResponse(Book book, String coverUrl) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getBookName(),
                book.getAuthor(),
                book.getCategory(),
                book.getPublisher(),
                book.getPublishedDate(),
                book.getQuantity(),
                book.getSales(),
                book.getPrice(),
                book.getDescription(),
                coverUrl,
                book.getBookCoverByte(),
                book.getCreationDate(),
                book.getEditDate(),
                book.getIsDeleted());
    }
}
