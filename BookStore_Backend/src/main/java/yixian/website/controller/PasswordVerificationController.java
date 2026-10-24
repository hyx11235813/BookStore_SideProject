package yixian.website.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.request.PasswordCheckRequest;
import yixian.website.security.AuthenticatedUser;
import yixian.website.security.CurrentUser;
import yixian.website.service.UserService;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class PasswordVerificationController {
    private final UserService userService;

    // 驗證使用者輸入的密碼是否等同於當前登入的用戶
    @PostMapping("/password")
    public Result<Boolean> matchPassword(@CurrentUser AuthenticatedUser currentUser,
                                         @RequestBody @Valid PasswordCheckRequest request) {
        return Result.ok(userService.passwordMatches(currentUser.id(), request.password()), SuccessMessages.GET_OK);
    }
}
