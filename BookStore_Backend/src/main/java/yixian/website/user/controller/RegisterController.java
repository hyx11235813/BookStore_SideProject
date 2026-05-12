package yixian.website.user.controller;

import org.springframework.web.bind.annotation.*;
import yixian.website.common.Result;
import yixian.website.model.Users;
import yixian.website.service.RegisterService;
import yixian.website.service.query.UserQueryService;
import yixian.website.util.MapUtil;
import yixian.website.verification.RegexValidator;




@RestController
@RequestMapping("/register")
public class RegisterController {
    private final RegisterService registerService;
    private final UserQueryService userQueryService;


    public RegisterController(RegisterService registerService, UserQueryService userQueryService1) {
        this.registerService = registerService;
        this.userQueryService = userQueryService1;
    }

    @PostMapping("/")
    public Result register(@RequestBody Users user) {
        registerService.register(user);
        return Result.saveSuccess(null, "註冊成功");

    }


    @GetMapping("/check-account/{account}")
     public Result verifyAccount(@PathVariable String account) {
        if (!RegexValidator.isValidAccount(account)) {
            return Result.getError("賬號格式錯誤");
        }
        boolean isExists = userQueryService.isAccountExists(account);

        return Result.getSuccess(
                MapUtil.isExists(isExists),
                isExists ? "賬號已被使用" : "賬號可以使用"
        );
    }

    @GetMapping("/check-email/{email}")
    public Result verifyEmail(@PathVariable String email) {
        if (!RegexValidator.isValidEmail(email)) {
            return Result.getError("信箱格式不正確");
        }
        boolean isExists = userQueryService.isEmailExists(email);
        return Result.getSuccess(
                MapUtil.isExists(isExists),
                isExists ? "信箱已被使用" : "信箱可以使用"
        );
    }


}
