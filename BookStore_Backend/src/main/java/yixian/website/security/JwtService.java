package yixian.website.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.stereotype.Component;
import yixian.website.config.JwtProperties;
import yixian.website.exception.UnauthorizedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 登入時簽發 token，之後每次請求由 {@link AuthInterceptor} 驗證。
 */
@Component
public class JwtService {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Duration timeToLive;

    public JwtService(JwtProperties properties) {
        this.algorithm = Algorithm.HMAC256(properties.secret());
        this.verifier = JWT.require(algorithm).build();
        this.timeToLive = Duration.ofHours(properties.expirationHours());
    }

    public String createToken(Integer userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Date.from(Instant.now().plus(timeToLive)))
                .sign(algorithm);
    }

    /**
     * 驗證簽章與有效期限，回傳 token 中的使用者 id
     */
    public Integer verifyAndGetUserId(String token) {
        try {
            return Integer.valueOf(verifier.verify(token).getSubject());
        } catch (TokenExpiredException e) {
            throw new UnauthorizedException("Token 已過期，請重新登入");
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new UnauthorizedException("身份驗證失敗，Token 無效或格式錯誤");
        }
    }
}
