package yixian.website.user.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.BookSearchRequestDto;
import yixian.website.model.Book;
import yixian.website.repository.BookRepository;
import yixian.website.service.query.BookQueryService;

import java.util.List;


@RestController
@RequestMapping("/user")

public class UserBookController {
    private final BookQueryService bookQueryService;
    private final BookRepository bookRepository;

    public UserBookController(BookQueryService bookQueryService, BookRepository bookRepository) {
        this.bookQueryService = bookQueryService;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    public Result getAllBooksByParam(@ModelAttribute BookSearchRequestDto dto) {
        return Result.getSuccess(bookQueryService.findBooksByParams(dto), "查詢成功");
    }


    @GetMapping("/books/top100-bestseller")
    public Result getTop100Bestseller(BookSearchRequestDto dto) {
        return Result.getSuccess(bookQueryService.findTop100Bestseller(dto), "查詢成功");
    }

    @GetMapping("/books/top100-new-releases")
    public Result getNewlyReleasedBooks(BookSearchRequestDto dto) {
        return Result.getSuccess(bookQueryService.findTop100NewRelease(dto), "查詢成功");
    }

    @GetMapping("/books/top10-new-releases")
    public Result getTop10NewBooks() {
        return Result.getSuccess(bookRepository.findTop10ByOrderByCreationDateDesc(), "查詢成功");
    }

    @GetMapping("/books/top10-bestseller")
    public Result getTop10Bestseller() {
        return Result.getSuccess(bookRepository.findTop10ByOrderBySalesDesc(), "查詢成功");
    }

    @GetMapping("/books/top10-bestseller/{category}")
    public Result getBooksByCategoryOrderBySales(@PathVariable String category) {
        List<Book> books = bookRepository.findTop10ByCategoryOrderBySalesDesc(category);
        return Result.getSuccess(books, SuccessMessages.GET_OK);
    }

    @GetMapping("/book/detail/{isbn}")
    public Result getBookByIsbn(@PathVariable String isbn) {
        return Result.getSuccess(bookQueryService.findBookByIsbn(isbn), SuccessMessages.GET_OK);
    }

    @GetMapping("/book/search/id/{id}")
    public Result getBookById(@PathVariable Integer id) {
        return Result.getSuccess(bookQueryService.findBookById(id), "查詢書籍成功");
    }

    @PostMapping("/book/search/ids")
    public Result getBooksById(@RequestBody BookSearchRequestDto dto) {
        return Result.getSuccess(bookQueryService.findBooksById(dto), "獲取書籍資訊成功");
    }
}
