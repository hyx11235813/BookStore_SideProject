package yixian.website.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.common.ErrorMessages;
import yixian.website.exception.InvalidDataException;
import yixian.website.model.PasswordResetToken;
import yixian.website.model.Users;
import yixian.website.repository.PasswordResetRepository;
import yixian.website.util.CommonUtils;
import yixian.website.verification.RegexValidator;

import java.util.Date;

@Service
public class EmailService {
    private static final String ACTIVATION_SUBJECT = "註冊驗證";
    private static final String PASSWORD_RESET_SUBJECT_TEMPLATE = "%s 是您的驗證碼";
    private static final String PASSWORD_RESET_BODY_TEMPLATE = "您正在修改密碼，驗證碼為：%s\n請在30分內完成驗證。";

    private final JavaMailSender javaMailSender;
    private final PasswordResetRepository passwordResetRepository;

    @Value("${app.url}")
    private String url;

    public EmailService(JavaMailSender javaMailSender, PasswordResetRepository passwordResetRepository) {
        this.javaMailSender = javaMailSender;
        this.passwordResetRepository = passwordResetRepository;
    }
    @Async
    public void sendActivationEmail(String email, String activationCode) {
        String activationLink = String.format("%s/api/email/activate/%s", url, activationCode);
        String messageBody = "請點擊以下連結進行驗證：" + activationLink;
        sendEmail(email, ACTIVATION_SUBJECT, messageBody);
    }

    public void sendPasswordResetTokenEmail(String email, String token) {
        if (!RegexValidator.isValidEmail(email)) {
            throw new InvalidDataException(email, ",信箱格式錯誤");
        }
        String subject = String.format(PASSWORD_RESET_SUBJECT_TEMPLATE, token);
        String body = String.format(PASSWORD_RESET_BODY_TEMPLATE, token);
        sendEmail(email, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
