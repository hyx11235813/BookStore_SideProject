package yixian.website.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import yixian.website.config.JwtProperties;
import yixian.website.exception.UnauthorizedException;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {
    private static final String SECRET = "unit-test-secret-with-at-least-32-chars";

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, 2));

    @Test
    void tokenRoundTripReturnsUserId() {
        String token = jwtService.createToken(42);

        assertEquals(42, jwtService.verifyAndGetUserId(token));
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String forged = new JwtService(new JwtProperties("another-secret-with-at-least-32-chars", 2)).createToken(1);

        assertThrows(UnauthorizedException.class, () -> jwtService.verifyAndGetUserId(forged));
    }

    @Test
    void expiredTokenIsRejected() {
        String expired = JWT.create()
                .withSubject("1")
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(Algorithm.HMAC256(SECRET));

        assertThrows(UnauthorizedException.class, () -> jwtService.verifyAndGetUserId(expired));
    }

    @Test
    void malformedTokenIsRejected() {
        assertThrows(UnauthorizedException.class, () -> jwtService.verifyAndGetUserId("not-a-jwt"));
    }
}
