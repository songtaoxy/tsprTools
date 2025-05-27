package com.st.modules.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 *     time: 即涵盖date, 也涵盖time
 * </pre>
 */
public class TimeUtils {


    /**
     * <pre>
     * - 将当前时间, 格式化成yyyy-MM-dd-HH-mm-ss格式的字符串
     * - 注意: DD: 是一年中的第几天，不是“日”，如需“几号”，应为dd
     *
     * - out put: 2024-05-25-14-27-356
     *
     * - test: ok
     * </pre>
     * @return
     */
    public static String time2Str() {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 注意 MM 是月份，mm 是分钟，dd 是日，DD 是一年中的第几天（不要误用）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formatted = now.format(formatter);
        return formatted;
    }

    public static String time2StrCust(String format) {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 注意 MM 是月份，mm 是分钟，dd 是日，DD 是一年中的第几天（不要误用）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formatted = now.format(formatter);
        return formatted;
    }
}
