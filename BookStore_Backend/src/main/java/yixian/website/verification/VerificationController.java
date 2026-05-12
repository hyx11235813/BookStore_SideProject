package yixian.website.verification;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.model.Users;
import yixian.website.util.TokenUtils;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;

    public VerificationController(PasswordEncoder passwordEncoder, TokenUtils tokenUtils) {
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
    }

    // 驗證使用者輸入的密碼是否等同於當前登入的用戶
    @PostMapping("/password")
    public Boolean matchPassword(@RequestBody Users user) {
        return passwordEncoder.matches(user.getPassword(), tokenUtils.getCurrentUser().getPassword());
    }


}