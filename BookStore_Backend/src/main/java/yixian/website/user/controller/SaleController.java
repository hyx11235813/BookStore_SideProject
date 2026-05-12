package yixian.website.user.controller;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.model.Book;
import yixian.website.model.Users;
import yixian.website.service.SaleService;
import yixian.website.service.query.BookQueryService;
import yixian.website.service.query.UserQueryService;

@RestController
@RequestMapping("/user")
public class SaleController {

    private final UserQueryService userQueryService;
    private final BookQueryService bookQueryService;
    private final SaleService saleService;

    public SaleController(UserQueryService userQueryService, BookQueryService bookQueryService, SaleService saleService) {
        this.userQueryService = userQueryService;
        this.bookQueryService = bookQueryService;
        this.saleService = saleService;
    }

    @Transactional
    @PostMapping("/book/sale")
    public Result saleBook(@RequestParam Integer userId, @RequestParam Integer bookId) {
        Users user = userQueryService.findUserById(userId);
        Book book = bookQueryService.findBookById(bookId);
        if (book.getQuantity() == null || book.getQuantity() <= 0) {
            return Result.saveError("庫存不足");
        }
        saleService.sale(user, book);
        return Result.updateSuccess(book, SuccessMessages.SAVE_OK);

    }
}
