package yixian.website.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.exception.InvalidDataException;
import yixian.website.model.Users;
import yixian.website.repository.UserRepository;
import yixian.website.service.query.UserQueryService;
import yixian.website.verification.RegexValidator;

import static yixian.website.util.RandomUtil.generateToken;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                           UserQueryService userQueryService1, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userQueryService = userQueryService1;
        this.emailService = emailService;
    }

    public void register(Users user) {
        validateUserRegistration(user);
        createUserAndSendActivationEmail(user);
    }

    private void validateUserRegistration(Users user) {
        //帳號可以不填，所以可以為null
        if (user.getAccount() != null && !RegexValidator.isValidAccount(user.getAccount())) {
            throw new InvalidDataException("帳號格式不正確");
        }
        if (!RegexValidator.isValidEmail(user.getEmail())) {
            throw new InvalidDataException("信箱格式不正確");
        }
        if (!RegexValidator.isValidPassword(user.getPassword())) {
            throw new InvalidDataException("密碼格式不正確");
        }
        if (!RegexValidator.isValidName(user.getName())) {
            throw new InvalidDataException("名稱格式不正確");
        }
        if (user.getAccount() != null && userQueryService.isAccountExists(user.getAccount())) {
            throw new InvalidDataException("帳號已被使用");
        }
        if (user.getEmail() != null && userQueryService.isEmailExists(user.getEmail())) {
            throw new InvalidDataException("信箱已被使用");
        }

    }
    @Transactional
    public Users createUserAndSendActivationEmail(Users user) {
        String activationCode = generateActivationCode();
        user.setActivationCode(activationCode);
        user.setRole("USER");
        user.setIsActive(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        sendActivationEmail(user.getEmail(), activationCode);
        userRepository.save(user);
        return user;
    }
    protected String generateActivationCode() {
        return generateToken();
    }

    private void sendActivationEmail(String email, String activationCode) {
        emailService.sendActivationEmail(email, activationCode);

    }

}
