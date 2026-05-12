package yixian.website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * 攔截器
 */

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

//    @Value("${jwt.exclude-paths}")
//    private String excludePaths;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())//配置攔截器規則
                .addPathPatterns("/admin/**","/user-info/**","/member-center/**")//攔截請求，這裡需要去掉/api前綴！！！
                .excludePathPatterns(); //需要特別放行的路徑
    }


}