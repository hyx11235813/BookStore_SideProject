package yixian.website.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import yixian.website.model.Users;
import yixian.website.service.query.UserQueryService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class TokenUtils {

    private static final Integer EXPIRATION_HOUR = 2;
    private final UserQueryService userQueryService;

    public TokenUtils(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public static String createToken(String userId, String sign) {
        Date expiresAt = Date.from(
                LocalDateTime.now().plusHours(EXPIRATION_HOUR)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        return JWT.create().withAudience(userId)
                .withExpiresAt(expiresAt) //過期時間
                .sign(Algorithm.HMAC256(sign));
    }

    public Users getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (!token.isBlank()) {
                String userId = JWT.decode(token).getAudience().get(0);
                return userQueryService.findUserById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


}
