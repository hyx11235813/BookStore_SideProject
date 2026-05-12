package yixian.website.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    private static final Integer EXPIRED_MINUTES = 30;

    public static Date taiwanCurrentTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
        return calendar.getTime();
    }

    public static Date calculateExpiryDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, EXPIRED_MINUTES);
        return calendar.getTime();
    }
}
