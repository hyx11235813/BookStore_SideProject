package yixian.website.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import yixian.website.dto.BookDto;
import yixian.website.dto.PageRequestDto;
import yixian.website.dto.SearchBookRequestDto;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.model.Book;

import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.service.BookManagementService;
import yixian.website.service.query.BookQueryService;
import yixian.website.util.ImageUtils;

@RestController
@RequestMapping("/admin")
public class AdminBookController {
    private final BookManagementService bookManagementService;
    private final BookQueryService bookQueryService;

    public AdminBookController(BookManagementService bookManagementService, BookQueryService bookQueryService) {
        this.bookManagementService = bookManagementService;
        this.bookQueryService = bookQueryService;
    }


    @PostMapping("/book")
    public Result saveBook(@ModelAttribute BookDto bookDto) {
        Book book = bookManagementService.saveOrUpdateBook(bookDto);
        return Result.saveSuccess(book, SuccessMessages.SAVE_OK);
    }

    @GetMapping("/books")
    public Page<Book> findBooksByPage(@ModelAttribute PageRequestDto pageRequestDto) {
        return bookQueryService.page(pageRequestDto.toPageable(), pageRequestDto.withoutDeletedBook());
    }

    @GetMapping("/books/deleted")
    public Page<Book> findDeletedBooksByPage(@ModelAttribute PageRequestDto pageRequestDto) {
        return bookQueryService.page(pageRequestDto.toPageable(), pageRequestDto.includeDeletedBook());
    }

    @GetMapping("/book/search/id/{id}")
    public Result getBookById(@PathVariable Integer id) {
        Book book = bookQueryService.findBookById(id);
        book.setBookCoverUrl(ImageUtils.convertToBase64(book.getBookCoverByte()));
        return Result.getSuccess(book, SuccessMessages.GET_OK);
    }

    @GetMapping("/books/search")
    public Result searchBooks(@ModelAttribute SearchBookRequestDto searchBookRequestDto,
                              @ModelAttribute PageRequestDto pageRequestDto) {
        Page<Book> books = bookQueryService.findBooks(searchBookRequestDto, pageRequestDto.toPageable());
        return Result.getSuccess(books, SuccessMessages.GET_OK);
    }

    @GetMapping("/book/search/isbn/{isbn}")
    public Result searchBookByIsbn(@PathVariable String isbn) {
        Book book = bookQueryService.findBookByIsbn(isbn);
        return Result.getSuccess(book, SuccessMessages.GET_OK);
    }

    @PutMapping("/book/{id}/deleted")
    public Result softDeleteBookById(@PathVariable Integer id) {
        try {
            bookManagementService.softDeleteBookById(id);
        } catch (EntityNotFoundException e) {
            return Result.saveError("找不到ID為" + id + "的書籍，下架失敗");
        }
        return Result.saveSuccess(null, SuccessMessages.SAVE_OK);
    }

    @PutMapping("/book/{id}/relist")
    public Result relistBook(@PathVariable Integer id) {
        try{
            bookManagementService.relistBook(id);
        }catch (EntityNotFoundException e){
            return Result.saveError("找不到ID為" + id + "的書籍，重新上架失敗");
        }
        return Result.saveSuccess(null, SuccessMessages.SAVE_OK);

    }
}
