package yixian.website.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.model.Users;
import yixian.website.service.AuthService;

@RestController
@RequestMapping("/user")
public class LoginController {
    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result login(@RequestBody Users user) {
        return Result.loginSuccess(authService.login(user));
    }

}
