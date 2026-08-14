package shipsensor.simulator.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 */
public final class TimeUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtil() {
    }

    /**
     * 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 增加秒
     */
    public static LocalDateTime plusSeconds(int seconds) {
        return LocalDateTime.now().plusSeconds(seconds);
    }

    /**
     * 格式化
     */
    public static String format(LocalDateTime time) {
        return FORMATTER.format(time);
    }

}