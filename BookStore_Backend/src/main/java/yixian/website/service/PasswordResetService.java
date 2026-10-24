package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.entity.PasswordResetToken;
import yixian.website.entity.User;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.PasswordResetTokenRepository;
import yixian.website.util.RandomCodes;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 忘記密碼流程：寄出驗證碼 -> 驗證驗證碼 -> 用驗證碼重設密碼
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private static final Duration CODE_VALIDITY = Duration.ofMinutes(30);

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void sendResetCode(String email) {
        User user = userService.findByEmail(email);
        tokenRepository.deleteByUserId(user.getId());

        String code = RandomCodes.sixDigits();
        tokenRepository.save(new PasswordResetToken(code, user, Date.from(Instant.now().plus(CODE_VALIDITY))));
        emailService.sendPasswordResetCodeEmail(user.getEmail(), code);
    }

    public void verifyCode(String email, String code) {
        findValidToken(email, code);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        PasswordResetToken token = findValidToken(email, code);
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        tokenRepository.deleteByUserId(user.getId());
    }

    private PasswordResetToken findValidToken(String email, String code) {
        User user = userService.findByEmail(email);
        PasswordResetToken token = tokenRepository.findByTokenAndUserId(code, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("驗證碼錯誤"));
        if (token.isExpiredAt(new Date())) {
            throw new InvalidDataException("驗證碼已過期");
        }
        return token;
    }
}
