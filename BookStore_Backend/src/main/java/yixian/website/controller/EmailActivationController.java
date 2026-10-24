package yixian.website.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.service.UserService;

/**
 * 使用者點擊驗證信中的連結後開通帳號，並導向前端對應的頁面
 */
@RestController
@RequestMapping("/email")
public class EmailActivationController {
    private static final String SUCCESS_PATH = "/register/successInfo";
    private static final String ERROR_PATH = "/error";

    private final UserService userService;
    private final String frontendUrl;

    public EmailActivationController(UserService userService, @Value("${app.url}") String frontendUrl) {
        this.userService = userService;
        this.frontendUrl = frontendUrl;
    }

    @GetMapping("/activate/{activationCode}")
    public ResponseEntity<Void> activate(@PathVariable String activationCode) {
        String targetPath;
        try {
            userService.activateAccount(activationCode);
            targetPath = SUCCESS_PATH;
        } catch (EntityNotFoundException e) {
            targetPath = ERROR_PATH;
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, frontendUrl + targetPath)
                .build();
    }
}
