package yixian.website.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.dto.request.RegisterRequest;
import yixian.website.dto.response.ExistenceResponse;
import yixian.website.exception.InvalidDataException;
import yixian.website.service.RegistrationService;
import yixian.website.service.UserService;
import yixian.website.validation.ValidationPatterns;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final UserService userService;

    @PostMapping("/")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        registrationService.register(request);
        return Result.ok("註冊成功");
    }

    @GetMapping("/check-account/{account}")
    public Result<ExistenceResponse> checkAccount(@PathVariable String account) {
        if (!ValidationPatterns.isValidAccount(account)) {
            throw new InvalidDataException("賬號格式錯誤");
        }
        boolean exists = userService.isAccountTaken(account);
        return Result.ok(new ExistenceResponse(exists), exists ? "賬號已被使用" : "賬號可以使用");
    }

    @GetMapping("/check-email/{email}")
    public Result<ExistenceResponse> checkEmail(@PathVariable String email) {
        if (!ValidationPatterns.isValidEmail(email)) {
            throw new InvalidDataException("信箱格式不正確");
        }
        boolean exists = userService.isEmailTaken(email);
        return Result.ok(new ExistenceResponse(exists), exists ? "信箱已被使用" : "信箱可以使用");
    }
}
