package yixian.website.util;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {

    // 生成 UUID token
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    // 產生 6 位數字驗證碼
    public static String generateRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(999999) + 1;
        return String.format("%06d", number);
    }
}
