package yixian.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import yixian.website.security.AdminInterceptor;
import yixian.website.security.AuthInterceptor;
import yixian.website.security.CurrentUserArgumentResolver;

import java.util.List;

/**
 * 設定哪些路徑需要驗證身份。
 * 注意：路徑不包含 /api 前綴（spring.mvc.servlet.path）。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String[] AUTHENTICATED_PATHS = {
            "/admin/**",
            "/user-info/**",
            "/verification/**",
            "/file/upload",
            "/user/cart/**",
            "/user/book/sale"
    };

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    public WebConfig(AuthInterceptor authInterceptor,
                     AdminInterceptor adminInterceptor,
                     CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.authInterceptor = authInterceptor;
        this.adminInterceptor = adminInterceptor;
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns(AUTHENTICATED_PATHS).order(0);
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**").order(1);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
