package yixian.website.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import yixian.website.config.JwtProperties;
import yixian.website.dto.request.LoginRequest;
import yixian.website.dto.response.LoginResponse;
import yixian.website.entity.User;
import yixian.website.exception.UnauthorizedException;
import yixian.website.repository.UserRepository;
import yixian.website.security.JwtService;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String PASSWORD = "abcd1234";

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = new JwtService(new JwtProperties("unit-test-secret-with-at-least-32-chars", 2));
    private AuthService authService;
    private User user;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        user = new User();
        user.setId(5);
        user.setAccount("tester01");
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setRole("USER");
        user.setIsActive(true);
    }

    @Test
    void loginWithAccountReturnsTokenAndRecordsLoginTime() {
        Date previousLogin = new Date(1_000);
        user.setLoginDate(previousLogin);
        when(userRepository.findByAccount("tester01")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(new LoginRequest("tester01", PASSWORD));

        assertEquals(5, jwtService.verifyAndGetUserId(response.token()));
        assertEquals(previousLogin, user.getLastLoginDate());
        assertNotNull(user.getLoginDate());
    }

    @Test
    void loginWithEmailFallsBackToEmailLookup() {
        when(userRepository.findByAccount("a@b.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(new LoginRequest("a@b.com", PASSWORD));

        assertNotNull(response.token());
    }

    @Test
    void wrongPasswordAndUnknownAccountGiveTheSameError() {
        when(userRepository.findByAccount("tester01")).thenReturn(Optional.of(user));
        when(userRepository.findByAccount("nobody")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nobody")).thenReturn(Optional.empty());

        UnauthorizedException wrongPassword = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("tester01", "wrong-pass1")));
        UnauthorizedException unknownUser = assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("nobody", PASSWORD)));

        assertEquals(wrongPassword.getMessage(), unknownUser.getMessage());
    }

    @Test
    void inactiveAccountDoesNotReceiveAToken() {
        user.setIsActive(false);
        when(userRepository.findByAccount("tester01")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(new LoginRequest("tester01", PASSWORD));

        assertNull(response.token());
        assertEquals(false, response.isActive());
    }
}
