package yixian.website.dto.request;

/**
 * 管理員新增或編輯使用者。id 為 null 代表新增。
 */
public record AdminUserSaveRequest(Integer id, String name, String email, String role, Boolean isActive) {
}
