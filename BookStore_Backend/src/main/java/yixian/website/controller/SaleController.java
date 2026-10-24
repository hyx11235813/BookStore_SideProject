package yixian.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.response.BookResponse;
import yixian.website.mapper.BookMapper;
import yixian.website.security.AuthenticatedUser;
import yixian.website.security.CurrentUser;
import yixian.website.service.SaleService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping("/book/sale")
    public Result<BookResponse> sellBook(@CurrentUser AuthenticatedUser currentUser, @RequestParam Integer bookId) {
        return Result.ok(BookMapper.toResponse(saleService.sell(currentUser.id(), bookId)), SuccessMessages.SAVE_OK);
    }
}
