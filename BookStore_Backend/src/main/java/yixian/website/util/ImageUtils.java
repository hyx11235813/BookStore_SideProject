package yixian.website.util;

import java.util.Base64;

public final class ImageUtils {

    private ImageUtils() {
    }

    //把書籍封面轉換爲base64 data URL，方便前端直接顯示
    public static String toDataUrl(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return "";
        }
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
