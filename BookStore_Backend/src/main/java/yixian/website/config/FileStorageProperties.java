package yixian.website.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.file")
public record FileStorageProperties(@DefaultValue("files") String uploadDir) {
}
