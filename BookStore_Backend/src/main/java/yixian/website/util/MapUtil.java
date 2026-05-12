package yixian.website.util;

import java.util.Map;

public class MapUtil {
    public static Map<String, Boolean> isExists(Boolean isExists) {
        return Map.of("isExist", isExists);
    }

}
