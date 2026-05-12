package yixian.website.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yixian.website.common.Result;
import yixian.website.dto.VerifyTokenRequestDto;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.model.PasswordResetToken;
import yixian.website.model.Users;
import yixian.website.repository.PasswordResetRepository;
import yixian.website.service.query.UserQueryService;

import java.util.Date;

import static yixian.website.util.DateTimeUtil.taiwanCurrentTime;


@Service
public class VerifyService {

    private final UserQueryService userQueryService;
    private final PasswordResetRepository passwordResetRepository;

    public VerifyService(UserQueryService userQueryService, PasswordResetRepository passwordResetRepository) {
        this.userQueryService = userQueryService;
        this.passwordResetRepository = passwordResetRepository;
    }

    public Result verifyPasswordReset(VerifyTokenRequestDto verifyTokenRequest) {
        Users user = userQueryService.findUserByEmail(verifyTokenRequest.getEmail());
        PasswordResetToken token = passwordResetRepository.findByTokenAndUser(verifyTokenRequest.getToken(), user)
                .orElseThrow(() -> new EntityNotFoundException("找不到持有該驗證碼: " + verifyTokenRequest.getToken() + " 的用戶"));
        if (hasTokenExpired(token)) {
            return Result.loginError(HttpStatus.FORBIDDEN.value(), "登入狀態已過期");
        }

        return Result.getSuccess(null, "驗證成功");

    }

    private boolean hasTokenExpired(PasswordResetToken passwordResetToken) {
        Date now = taiwanCurrentTime();
        return now.after(passwordResetToken.getExpiryDate());
    }

}
