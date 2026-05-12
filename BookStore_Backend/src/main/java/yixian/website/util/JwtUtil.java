package yixian.website.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class JwtUtil {

    private static String secretKey;

    static {
        secretKey = generateSecretKey();
    }

    private static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    public static String getSecretKey() {
        return secretKey;
    }
}
