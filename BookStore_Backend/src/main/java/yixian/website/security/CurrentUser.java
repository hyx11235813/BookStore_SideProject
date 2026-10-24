package yixian.website.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 標在 controller 方法參數上，注入目前登入的 {@link AuthenticatedUser}。
 * 只能用在 {@link AuthInterceptor} 有攔截的路徑。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
