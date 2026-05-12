package yixian.website.user.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.config.SkipAuthCheck;
import yixian.website.dto.EmailVerifyRequestDto;
import yixian.website.dto.ResetPasswordRequestDto;
import yixian.website.dto.VerifyTokenRequestDto;
import yixian.website.model.Users;
import yixian.website.service.PasswordResetService;
import yixian.website.service.query.UserQueryService;
import yixian.website.service.VerifyService;

@RestController
@RequestMapping("/user/forgot-password")
public class ResetPasswordController {
    private final UserQueryService userQueryService;
    private final VerifyService verifyService;
    private final PasswordResetService passwordResetService;


    public ResetPasswordController(UserQueryService userQueryService,
                                   VerifyService verifyService1, PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
        this.userQueryService = userQueryService;
        this.verifyService = verifyService1;
    }

    @PostMapping("/request")
    public Result requestResetPassword(@RequestBody EmailVerifyRequestDto requestDto) {
        Users user = userQueryService.findUserByEmail(requestDto.getEmail());
        passwordResetService.handlePasswordResetToken(user);
        return Result.saveSuccess(null, "重設密碼信件已發送至信箱");
    }


    @PostMapping("/verify-token")
    public Result verifyResetPasswordToken(@RequestBody @Valid VerifyTokenRequestDto verifyTokenRequest) {
        return verifyService.verifyPasswordReset(verifyTokenRequest);
    }

    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody @Valid ResetPasswordRequestDto requestDto) {
        return passwordResetService.resetPassword(requestDto);
    }

}
