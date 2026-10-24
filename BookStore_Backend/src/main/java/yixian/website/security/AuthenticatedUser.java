package yixian.website.security;

import yixian.website.exception.ForbiddenException;

/**
 * 通過 token 驗證的目前使用者
 */
public record AuthenticatedUser(Integer id, String role) {
    static final String REQUEST_ATTRIBUTE = AuthenticatedUser.class.getName();

    /**
     * 只能操作自己的資料
     */
    public void requireSameUser(Integer userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException("無訪問權限");
        }
    }
}
