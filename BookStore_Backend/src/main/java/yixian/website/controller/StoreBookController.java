package yixian.website.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.ErrorMessages;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.request.BookIdsRequest;
import yixian.website.dto.request.StoreBookQuery;
import yixian.website.dto.response.BookResponse;
import yixian.website.dto.response.PageResponse;
import yixian.website.entity.Book;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.mapper.BookMapper;
import yixian.website.service.BookService;

import java.util.List;

/**
 * 使用者端（商店）的書籍查詢，不需要登入
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class StoreBookController {
    private final BookService bookService;

    @GetMapping("/books")
    public Result<PageResponse<BookResponse>> searchBooks(@ModelAttribute StoreBookQuery query) {
        return Result.ok(toPage(bookService.searchForStore(query)), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/top100-bestseller")
    public Result<PageResponse<BookResponse>> getTop100Bestsellers(@ModelAttribute StoreBookQuery query) {
        return Result.ok(toPage(bookService.findTop100Bestsellers(query)), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/top100-new-releases")
    public Result<PageResponse<BookResponse>> getTop100NewReleases(@ModelAttribute StoreBookQuery query) {
        return Result.ok(toPage(bookService.findTop100NewReleases(query)), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/top10-new-releases")
    public Result<List<BookResponse>> getTop10NewReleases() {
        return Result.ok(toList(bookService.findTop10NewReleases()), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/top10-bestseller")
    public Result<List<BookResponse>> getTop10Bestsellers() {
        return Result.ok(toList(bookService.findTop10Bestsellers()), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/top10-bestseller/{category}")
    public Result<List<BookResponse>> getTop10BestsellersByCategory(@PathVariable String category) {
        return Result.ok(toList(bookService.findTop10BestsellersByCategory(category)), SuccessMessages.GET_OK);
    }

    @GetMapping("/book/detail/{isbn}")
    public Result<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        Book book = bookService.findByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.DATA_NOT_FOUND));
        return Result.ok(BookMapper.toDetailResponse(book), SuccessMessages.GET_OK);
    }

    @GetMapping("/book/search/id/{id}")
    public Result<BookResponse> getBookById(@PathVariable Integer id) {
        return Result.ok(BookMapper.toResponse(bookService.findById(id)), SuccessMessages.GET_OK);
    }

    @PostMapping("/book/search/ids")
    public Result<List<BookResponse>> getBooksByIds(@RequestBody @Valid BookIdsRequest request) {
        return Result.ok(toList(bookService.findByIds(request)), SuccessMessages.GET_OK);
    }

    private PageResponse<BookResponse> toPage(Page<Book> books) {
        return PageResponse.from(books, BookMapper::toResponse);
    }

    private List<BookResponse> toList(List<Book> books) {
        return books.stream().map(BookMapper::toResponse).toList();
    }
}
