package yixian.website.util;

import java.security.SecureRandom;
import java.util.UUID;

public final class RandomCodes {
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomCodes() {
    }

    // 生成 UUID token
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    // 產生 6 位數字驗證碼
    public static String sixDigits() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
