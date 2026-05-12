package yixian.website.util;

import org.springframework.stereotype.Component;

@Component
public class CommonUtils {

    public static  <T> T defaultIfNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

}
