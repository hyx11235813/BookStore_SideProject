package yixian.website.dto.response;

/**
 * 登入結果。帳號尚未開通時不會發放 token。
 */
public record LoginResponse(String token, Integer id, String name, String role, Boolean isActive, String avatar) {
}
