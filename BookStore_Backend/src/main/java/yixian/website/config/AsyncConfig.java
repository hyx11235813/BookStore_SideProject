package yixian.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 讓 @Async 生效（例如寄送驗證信不阻塞請求）
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
