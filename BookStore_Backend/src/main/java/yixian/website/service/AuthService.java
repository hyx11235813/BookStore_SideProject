package yixian.website.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yixian.website.model.Users;
import yixian.website.repository.UserRepository;
import yixian.website.common.Code;
import yixian.website.common.ErrorMessages;
import yixian.website.exception.InvalidDataException;
import yixian.website.exception.ServiceException;
import yixian.website.util.DateTimeUtil;
import yixian.website.util.JwtUtil;
import yixian.website.util.TokenUtils;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users login(Users user) {
        Users existingUser = findUserByIdentifier(user.getLoginIdentifier());
        validatePassword(user.getPassword(), existingUser.getPassword());
        updateLoginInfo(existingUser);
        return existingUser;
    }

    private Users findUserByIdentifier(String identifier) {
        return userRepository.findUserByAccount(identifier)
                .or(() -> userRepository.findUserByEmail(identifier))
                .orElseThrow(() -> new ServiceException(Code.UNAUTHORIZED, "帳號或信箱錯誤"));
    }


    public void validatePassword(String inputPassword, String storedPassword) {
        if (!StringUtils.hasText(inputPassword)) {
            throw new InvalidDataException("密碼輸入不得為空");
        }
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new ServiceException(Code.UNAUTHORIZED, "密碼錯誤");
        }
    }

    private void updateLoginInfo(Users user) {
        user.setLastLoginDate(user.getLoginDate());
        user.setLoginDate(DateTimeUtil.taiwanCurrentTime());
        user.setToken(TokenUtils.createToken(user.getId().toString(), JwtUtil.getSecretKey()));
        userRepository.save(user);
    }
}
