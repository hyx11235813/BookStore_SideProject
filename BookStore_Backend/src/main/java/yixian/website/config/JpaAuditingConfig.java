package yixian.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 啟用 @CreatedDate / @LastModifiedDate
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
