package yixian.website.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final String ACTIVATION_SUBJECT = "註冊驗證";
    private static final String PASSWORD_RESET_SUBJECT_TEMPLATE = "%s 是您的驗證碼";
    private static final String PASSWORD_RESET_BODY_TEMPLATE = "您正在修改密碼，驗證碼為：%s\n請在30分內完成驗證。";

    private final JavaMailSender javaMailSender;
    private final String baseUrl;

    public EmailService(JavaMailSender javaMailSender, @Value("${app.url}") String baseUrl) {
        this.javaMailSender = javaMailSender;
        this.baseUrl = baseUrl;
    }

    @Async
    public void sendActivationEmail(String email, String activationCode) {
        String activationLink = String.format("%s/api/email/activate/%s", baseUrl, activationCode);
        sendEmail(email, ACTIVATION_SUBJECT, "請點擊以下連結進行驗證：" + activationLink);
    }

    public void sendPasswordResetCodeEmail(String email, String code) {
        sendEmail(email,
                String.format(PASSWORD_RESET_SUBJECT_TEMPLATE, code),
                String.format(PASSWORD_RESET_BODY_TEMPLATE, code));
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
