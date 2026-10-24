package yixian.website.service;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yixian.website.config.FileStorageProperties;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.exception.ServiceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 把使用者上傳的圖片存到本地磁碟
 */
@Service
public class FileStorageService {
    private static final Map<String, String> IMAGE_CONTENT_TYPES = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "webp", "image/webp");

    private final Path root;

    public FileStorageService(FileStorageProperties properties) {
        this.root = Path.of(properties.uploadDir()).toAbsolutePath().normalize();
    }

    /**
     * @return 儲存後的檔名（以隨機名稱儲存，避免重名與路徑注入）
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDataException("文件為空");
        }
        String extension = extensionOf(file.getOriginalFilename());
        if (!IMAGE_CONTENT_TYPES.containsKey(extension)) {
            throw new InvalidDataException("只支援圖片檔案");
        }
        String fileName = UUID.randomUUID() + "." + extension;
        try {
            Files.createDirectories(root);
            file.transferTo(root.resolve(fileName));
        } catch (IOException e) {
            throw new ServiceException("文件儲存失敗", e);
        }
        return fileName;
    }

    public Resource load(String fileName) {
        Path path = root.resolve(fileName).normalize();
        if (!path.startsWith(root) || !Files.isRegularFile(path)) {
            throw new EntityNotFoundException("文件不存在");
        }
        return new PathResource(path);
    }

    public String contentTypeOf(String fileName) {
        return IMAGE_CONTENT_TYPES.getOrDefault(extensionOf(fileName), "application/octet-stream");
    }

    private String extensionOf(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
