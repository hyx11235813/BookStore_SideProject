package yixian.website.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import yixian.website.common.Code;
import yixian.website.exception.ServiceException;
import yixian.website.model.Users;
import yixian.website.repository.UserRepository;
import yixian.website.util.JwtUtil;


/**
 * 用戶發送請求時，攔截器檢查token是否有效。
 * InterceptorConfig中配置攔截器規則
 * 在處理請求時會先判斷權限（token正不正確）
 * 相關程式：
 * InterceptorConfig.java （addInterceptors()。用來配置那些路徑需要攔截）
 * TokenUtils.java （createToken方法。登入時調用 生成token）
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {


    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();


        // 1. 檢查是否Controller有跳過檢查的註解
        if (isSkipAuth(handler)) {
            return true;
        }
        // 2.獲取Token
        String token = extractToken(request);

        // 3. 解析token中的userId
        String userId = extractUserIdFromToken(token);

        // 4.驗證用戶
        Users user = validateUserExistsById(userId);

        // 5.檢查用戶權限
        validateUserRole(user, uri);

        // 6.驗證token
        verifyToken(token);

        return true;
    }

    private boolean isSkipAuth(Object handler) {
        if (handler instanceof HandlerMethod) {
            SkipAuthCheck skipAuthCheck = ((HandlerMethod) handler).getMethodAnnotation(SkipAuthCheck.class);
            return skipAuthCheck != null;  //如果Controller上有使用@SkipAuthCheck注解，則返回true
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("token"); //從請求頭獲取
        if (token == null || token.isBlank()) {
            token = request.getParameter("token");//從URL獲取
        }
        if (token == null || token.isBlank()) {
            throw new ServiceException(Code.UNAUTHORIZED, "獲取不到用戶的Token");
        }
        return token;
    }


    private String extractUserIdFromToken(String token) {
        try {
            return JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(Code.UNAUTHORIZED, "身份驗證失敗，Token 無效或格式錯誤");
        }
    }

    private Users validateUserExistsById(String id) {
        return userRepository.findById(Integer.valueOf(id)).
                orElseThrow(() -> new ServiceException(Code.NOT_FOUND, "身份驗證失敗，用戶 ID 為 " + id + " 的用戶不存在"));

    }

    private void validateUserRole(Users user, String uri) {
        //用戶角色為USER，不能訪問admin開的的請求路徑
        if ("USER".equalsIgnoreCase(user.getRole()) && uri.startsWith("/api/admin/")) {
            throw new ServiceException(Code.FORBIDDEN, "無訪問權限");
        }
    }


    private void verifyToken(String token) {
        String secretKey = JwtUtil.getSecretKey();
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceException(Code.UNAUTHORIZED, "Token 驗證失敗");
        }
    }

}