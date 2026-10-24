package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.dto.request.LoginRequest;
import yixian.website.dto.response.LoginResponse;
import yixian.website.entity.User;
import yixian.website.exception.UnauthorizedException;
import yixian.website.repository.UserRepository;
import yixian.website.security.JwtService;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * 帳號尚未開通時只回傳使用者狀態，不發放 token
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = findByAccountOrEmail(request.loginIdentifier())
                .filter(found -> passwordEncoder.matches(request.password(), found.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("帳號或密碼錯誤"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            return toResponse(user, null);
        }
        recordLogin(user);
        return toResponse(user, jwtService.createToken(user.getId()));
    }

    private Optional<User> findByAccountOrEmail(String identifier) {
        return userRepository.findByAccount(identifier).or(() -> userRepository.findByEmail(identifier));
    }

    private void recordLogin(User user) {
        user.setLastLoginDate(user.getLoginDate());
        user.setLoginDate(new Date());
    }

    private LoginResponse toResponse(User user, String token) {
        return new LoginResponse(token, user.getId(), user.getName(), user.getRole(), user.getIsActive(), user.getAvatar());
    }
}
