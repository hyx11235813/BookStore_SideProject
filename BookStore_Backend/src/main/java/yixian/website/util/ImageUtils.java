package yixian.website.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class ImageUtils {

    // 將圖片 URL 轉換為字節數組
    public static byte[] processImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("圖片 URL 不能為空");
        }

        try (InputStream inputStream = new URI(imageUrl).toURL().openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();

        } catch (URISyntaxException e) {
            throw new RuntimeException("URL 格式錯誤：" + imageUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("讀取圖片數據失敗：" + imageUrl, e);
        }
    }

    //把書籍封面轉換爲base64，方便前端直接顯示
    public static String convertToBase64(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return "";
        }
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
