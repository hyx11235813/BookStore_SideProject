package yixian.website.util;

import org.springframework.stereotype.Component;
import yixian.website.common.ErrorMessages;
import yixian.website.exception.InvalidDataException;
import yixian.website.exception.ServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLConnection;
import java.util.Set;

/**
 * 下載外部 API 提供的書籍封面。
 * 圖片網址由使用者提供，為避免伺服器被利用來存取內部網路 (SSRF)，只允許 http(s) 且不得指向內部位址。
 */
@Component
public class RemoteImageDownloader {
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final int TIMEOUT_MILLIS = 5_000;
    private static final int MAX_IMAGE_BYTES = 5 * 1024 * 1024;

    public byte[] download(String imageUrl) {
        URI uri = parseAndValidate(imageUrl);
        try {
            URLConnection connection = uri.toURL().openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLIS);
            connection.setReadTimeout(TIMEOUT_MILLIS);
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] bytes = inputStream.readNBytes(MAX_IMAGE_BYTES + 1);
                if (bytes.length > MAX_IMAGE_BYTES) {
                    throw new InvalidDataException("圖片大小超過限制");
                }
                return bytes;
            }
        } catch (IOException e) {
            throw new ServiceException(ErrorMessages.PROCESS_IMG_ERROR, e);
        }
    }

    private URI parseAndValidate(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new InvalidDataException("圖片 URL 不能為空");
        }
        try {
            URI uri = URI.create(imageUrl.trim());
            if (uri.getScheme() == null || !ALLOWED_SCHEMES.contains(uri.getScheme().toLowerCase())) {
                throw new InvalidDataException("圖片 URL 格式錯誤");
            }
            if (uri.getHost() == null || isInternalAddress(InetAddress.getByName(uri.getHost()))) {
                throw new InvalidDataException("不允許的圖片 URL");
            }
            return uri;
        } catch (IllegalArgumentException | IOException e) {
            throw new InvalidDataException("圖片 URL 格式錯誤");
        }
    }

    private boolean isInternalAddress(InetAddress address) {
        return address.isAnyLocalAddress() || address.isLoopbackAddress()
                || address.isSiteLocalAddress() || address.isLinkLocalAddress();
    }
}
