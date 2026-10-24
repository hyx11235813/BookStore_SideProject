package yixian.website.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.dto.request.ForgotPasswordRequest;
import yixian.website.dto.request.ResetPasswordRequest;
import yixian.website.dto.request.VerifyResetTokenRequest;
import yixian.website.service.PasswordResetService;

@RestController
@RequestMapping("/user/forgot-password")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public Result<Void> requestResetPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        passwordResetService.sendResetCode(request.email());
        return Result.ok("重設密碼信件已發送至信箱");
    }

    @PostMapping("/verify-token")
    public Result<Void> verifyResetPasswordToken(@RequestBody @Valid VerifyResetTokenRequest request) {
        passwordResetService.verifyCode(request.email(), request.token());
        return Result.ok("驗證成功");
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.email(), request.token(), request.password());
        return Result.ok("密碼已成功更新");
    }
}
