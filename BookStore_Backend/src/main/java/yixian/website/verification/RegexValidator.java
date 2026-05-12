package yixian.website.verification;

import java.util.regex.Pattern;

public class RegexValidator {
    //帳號格式，6-16位，只能包含字母和數字
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,16}$");
    //email格式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9\\._%+\\-]+@[A-Za-z0-9\\.\\-]+\\.[A-Za-z]{2,}$");
    //密碼格式，8-16位，至少包含一個數字和一個字母，不包含空格
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?!.* ).{8,16}$");
    //名稱格式，1-16位，只能包含中文和字母
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,16}$");


    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidAccount(String account) {
        return account != null && ACCOUNT_PATTERN.matcher(account).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }


}
