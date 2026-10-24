package yixian.website.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import yixian.website.entity.User;
import yixian.website.exception.UnauthorizedException;
import yixian.website.repository.UserRepository;

/**
 * 驗證請求中的 token，通過後把使用者放進 request，之後可用 {@link CurrentUser} 取得。
 * 要攔截哪些路徑請看 WebConfig。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final String TOKEN_HEADER = "token";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthInterceptor(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = extractToken(request);
        Integer userId = jwtService.verifyAndGetUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("身份驗證失敗，用戶 ID 為 " + userId + " 的用戶不存在"));
        request.setAttribute(AuthenticatedUser.REQUEST_ATTRIBUTE, new AuthenticatedUser(user.getId(), user.getRole()));
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isBlank()) {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
                token = authorization.substring(BEARER_PREFIX.length());
            }
        }
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("獲取不到用戶的Token");
        }
        return token;
    }
}
