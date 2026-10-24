package yixian.website.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import yixian.website.common.Roles;
import yixian.website.exception.ForbiddenException;
import yixian.website.exception.UnauthorizedException;

/**
 * 只有管理員能通過。必須排在 {@link AuthInterceptor} 之後。
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object user = request.getAttribute(AuthenticatedUser.REQUEST_ATTRIBUTE);
        if (!(user instanceof AuthenticatedUser authenticatedUser)) {
            throw new UnauthorizedException("尚未登入");
        }
        if (!Roles.ADMIN.equalsIgnoreCase(authenticatedUser.role())) {
            throw new ForbiddenException("無訪問權限");
        }
        return true;
    }
}
