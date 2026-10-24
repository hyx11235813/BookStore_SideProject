package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.common.Roles;
import yixian.website.dto.request.RegisterRequest;
import yixian.website.entity.User;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.UserRepository;
import yixian.website.util.RandomCodes;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {
        validateNotTaken(request);

        User user = new User();
        user.setAccount(request.account());
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Roles.USER);
        user.setIsActive(false);
        user.setActivationCode(RandomCodes.uuid());
        userRepository.save(user);

        emailService.sendActivationEmail(user.getEmail(), user.getActivationCode());
    }

    private void validateNotTaken(RegisterRequest request) {
        if (request.account() != null && userService.isAccountTaken(request.account())) {
            throw new InvalidDataException("帳號已被使用");
        }
        if (userService.isEmailTaken(request.email())) {
            throw new InvalidDataException("信箱已被使用");
        }
    }
}
