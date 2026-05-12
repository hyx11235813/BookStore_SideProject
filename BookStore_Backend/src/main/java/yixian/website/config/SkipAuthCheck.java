package yixian.website.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 使用在方法上，表示該請求不需要驗證
 */
public @interface SkipAuthCheck {
}
