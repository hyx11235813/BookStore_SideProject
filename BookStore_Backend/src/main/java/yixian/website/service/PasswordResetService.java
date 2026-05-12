package yixian.website.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.common.Result;
import yixian.website.dto.ResetPasswordRequestDto;
import yixian.website.exception.InvalidDataException;
import yixian.website.model.PasswordResetToken;
import yixian.website.model.Users;
import yixian.website.repository.PasswordResetRepository;
import yixian.website.repository.UserRepository;
import yixian.website.service.query.UserQueryService;
import yixian.website.verification.RegexValidator;

import java.util.Date;
import java.util.List;

import static yixian.website.util.DateTimeUtil.calculateExpiryDate;
import static yixian.website.util.RandomUtil.generateRandomNumber;

@Service
public class PasswordResetService {
    private final EmailService emailService;
    private final UserQueryService userQueryService;
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(EmailService emailService, UserQueryService userQueryService, PasswordResetRepository passwordResetRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.userQueryService = userQueryService;
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void handlePasswordResetToken(Users user) {
        PasswordResetToken newToken = createPasswordResetToken(user);
        emailService.sendPasswordResetTokenEmail(user.getEmail(), newToken.getToken());
        deleteOldTokensByUser(user);
        saveNewToken(newToken);
    }

    private void deleteOldTokensByUser(Users user) {
        List<PasswordResetToken> tokens = passwordResetRepository.findByUser_Id(user.getId());
        if (!tokens.isEmpty()) {
            passwordResetRepository.deleteAll(tokens);
        }
    }

    private PasswordResetToken createPasswordResetToken(Users user) {
        String token = generateRandomNumber();
        Date expireDate = calculateExpiryDate();
        return new PasswordResetToken(token, user, expireDate);
    }

    private void saveNewToken(PasswordResetToken token) {
        passwordResetRepository.save(token);
    }

    public Result resetPassword(ResetPasswordRequestDto requestDto) {
        Users existingUser = userQueryService.findUserByEmail(requestDto.getEmail());
        if (!RegexValidator.isValidPassword(requestDto.getPassword())) {
            throw new InvalidDataException("密碼格式錯誤");
        }
        existingUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(existingUser);

        return Result.saveSuccess(null, "密碼已成功更新");
    }

}
