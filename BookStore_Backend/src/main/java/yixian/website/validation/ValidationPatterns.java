package yixian.website.validation;

import java.util.regex.Pattern;

/**
 * 前後端共用的輸入格式規則。常數給 Bean Validation 的 @Pattern 使用，isValidXxx 給程式內判斷使用。
 */
public final class ValidationPatterns {
    //帳號格式，6-16位，只能包含字母和數字
    public static final String ACCOUNT = "^[a-zA-Z0-9]{6,16}$";
    //email格式
    public static final String EMAIL = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$";
    //密碼格式，8-16位，至少包含一個數字和一個字母，不包含空格
    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(?!.* ).{8,16}$";
    //名稱格式，1-16位，只能包含中文、字母和數字
    public static final String NAME = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,16}$";

    private static final Pattern ACCOUNT_PATTERN = Pattern.compile(ACCOUNT);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD);
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME);

    private ValidationPatterns() {
    }

    public static boolean isValidAccount(String account) {
        return matches(ACCOUNT_PATTERN, account);
    }

    public static boolean isValidEmail(String email) {
        return matches(EMAIL_PATTERN, email);
    }

    public static boolean isValidPassword(String password) {
        return matches(PASSWORD_PATTERN, password);
    }

    public static boolean isValidName(String name) {
        return matches(NAME_PATTERN, name);
    }

    private static boolean matches(Pattern pattern, String value) {
        return value != null && pattern.matcher(value).matches();
    }
}
