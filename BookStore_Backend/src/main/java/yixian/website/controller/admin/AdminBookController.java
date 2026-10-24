package yixian.website.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.request.BookSaveRequest;
import yixian.website.dto.request.BookSearchRequest;
import yixian.website.dto.request.PageParams;
import yixian.website.dto.response.BookResponse;
import yixian.website.dto.response.PageResponse;
import yixian.website.mapper.BookMapper;
import yixian.website.service.BookService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;

    @PostMapping("/book")
    public Result<BookResponse> saveBook(@ModelAttribute @Valid BookSaveRequest request) {
        return Result.ok(BookMapper.toResponse(bookService.save(request)), SuccessMessages.SAVE_OK);
    }

    @GetMapping("/books")
    public Result<PageResponse<BookResponse>> findBooks(@ModelAttribute PageParams page) {
        return Result.ok(PageResponse.from(bookService.findPage(page, false), BookMapper::toResponse), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/deleted")
    public Result<PageResponse<BookResponse>> findDeletedBooks(@ModelAttribute PageParams page) {
        return Result.ok(PageResponse.from(bookService.findPage(page, true), BookMapper::toResponse), SuccessMessages.GET_OK);
    }

    @GetMapping("/books/search")
    public Result<PageResponse<BookResponse>> searchBooks(@ModelAttribute BookSearchRequest search,
                                                          @ModelAttribute PageParams page) {
        return Result.ok(PageResponse.from(bookService.searchForAdmin(search, page), BookMapper::toResponse), SuccessMessages.GET_OK);
    }

    @GetMapping("/book/search/id/{id}")
    public Result<BookResponse> getBookById(@PathVariable Integer id) {
        return Result.ok(BookMapper.toDetailResponse(bookService.findById(id)), SuccessMessages.GET_OK);
    }

    //新增書籍前檢查 ISBN 是否已存在，不存在時 data 為 null
    @GetMapping("/book/search/isbn/{isbn}")
    public Result<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.findByIsbn(isbn).map(BookMapper::toDetailResponse).orElse(null);
        return Result.ok(book, SuccessMessages.GET_OK);
    }

    @PutMapping("/book/{id}/deleted")
    public Result<Void> softDeleteBook(@PathVariable Integer id) {
        bookService.softDelete(id);
        return Result.ok(SuccessMessages.SAVE_OK);
    }

    @PutMapping("/book/{id}/relist")
    public Result<Void> relistBook(@PathVariable Integer id) {
        bookService.relist(id);
        return Result.ok(SuccessMessages.SAVE_OK);
    }
}
