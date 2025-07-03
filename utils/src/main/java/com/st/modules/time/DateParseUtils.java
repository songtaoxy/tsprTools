package com.st.modules.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * <pre>
 *     通用的字符串转日期工具类
 *     - 支持 LocalDateTime、LocalDate、LocalTime 等常用类型，线程安全，
 *     - 基于 Java 8+
 *     - 方法重载: 支持带pattern, 及不带patter
 * </pre>
 *
 * Usage
 * <pre>
 *     {@code
 *         LocalDateTime dt = DateParseUtils.parseToDateTime("2025-07-03 10:30:00", "yyyy-MM-dd HH:mm:ss");
 *         LocalDate d = DateParseUtils.parseToDate("2025-07-03", "yyyy-MM-dd");
 *         LocalTime t = DateParseUtils.parseToTime("10:30:00", "HH:mm:ss");
 *
 *         System.out.println(dt); // 2025-07-03T10:30
 *         System.out.println(d);  // 2025-07-03
 *         System.out.println(t);  // 10:30
 *
 *
 *         LocalDateTime dt2 = DateParseUtils.parseToDateTime("2025-07-03 10:20:30");
 *         LocalDate date = DateParseUtils.tryParseToDate("2025-07-03");
 *         LocalTime time = DateParseUtils.parseToTime("10:20:30");
 *
 *         System.out.println(dt2); // 2025-07-03T10:20:30
 *         System.out.println(date);  // 2025-07-03
 *         System.out.println(time);  // 10:30
 *     }
 * </pre>
 *
 * 后续待扩展
 * <pre>
 *     - 将 DateTimeFormatter 缓存为静态常量（避免重复构造）
 *     - 添加字符串转 ZonedDateTime、Instant 等支持
 * </pre>
 */
public class DateParseUtils {

    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";



    // ===========================
    // --- 基础方法（带 pattern） ---
    // ===========================

    /**
     * 解析为 LocalDateTime（日期 + 时间）
     * @param dateTimeStr 输入的日期字符串
     * @param pattern 日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return LocalDateTime 实例
     * @throws DateTimeParseException 格式不匹配时报错
     */
    public static LocalDateTime parseToDateTime(String dateTimeStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * 解析为 LocalDate（仅日期）
     * @param dateStr 输入的日期字符串
     * @param pattern 日期格式，例如 "yyyy-MM-dd"
     * @return LocalDate 实例
     * @throws DateTimeParseException 格式不匹配时报错
     */
    public static LocalDate parseToDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * 解析为 LocalTime（仅时间）
     * @param timeStr 输入的时间字符串
     * @param pattern 时间格式，例如 "HH:mm:ss"
     * @return LocalTime 实例
     * @throws DateTimeParseException 格式不匹配时报错
     */
    public static LocalTime parseToTime(String timeStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeStr, formatter);
    }

    /**
     * 安全解析为 LocalDateTime，异常返回 null
     */
    public static LocalDateTime tryParseToDateTime(String dateTimeStr, String pattern) {
        try {
            return parseToDateTime(dateTimeStr, pattern);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 安全解析为 LocalDate，异常返回 null
     */
    public static LocalDate tryParseToDate(String dateStr, String pattern) {
        try {
            return parseToDate(dateStr, pattern);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 安全解析为 LocalTime，异常返回 null
     */
    public static LocalTime tryParseToTime(String timeStr, String pattern) {
        try {
            return parseToTime(timeStr, pattern);
        } catch (Exception e) {
            return null;
        }
    }


    // ===========================
    // --- 不带 pattern, 使用默认格式 ---
    // ===========================
    public static LocalDateTime parseToDateTime(String dateTimeStr) {
        return parseToDateTime(dateTimeStr, DEFAULT_DATETIME_PATTERN);
    }

    public static LocalDate parseToDate(String dateStr) {
        return parseToDate(dateStr, DEFAULT_DATE_PATTERN);
    }

    public static LocalTime parseToTime(String timeStr) {
        return parseToTime(timeStr, DEFAULT_TIME_PATTERN);
    }

    public static LocalDateTime tryParseToDateTime(String dateTimeStr) {
        return tryParseToDateTime(dateTimeStr, DEFAULT_DATETIME_PATTERN);
    }

    public static LocalDate tryParseToDate(String dateStr) {
        return tryParseToDate(dateStr, DEFAULT_DATE_PATTERN);
    }

    public static LocalTime tryParseToTime(String timeStr) {
        return tryParseToTime(timeStr, DEFAULT_TIME_PATTERN);
    }


    public static void main(String[] args) {
        LocalDateTime dt = DateParseUtils.parseToDateTime("2025-07-03 10:30:00", "yyyy-MM-dd HH:mm:ss");
        LocalDate d = DateParseUtils.parseToDate("2025-07-03", "yyyy-MM-dd");
        LocalTime t = DateParseUtils.parseToTime("10:30:00", "HH:mm:ss");

        System.out.println(dt); // 2025-07-03T10:30
        System.out.println(d);  // 2025-07-03
        System.out.println(t);  // 10:30


        LocalDateTime dt2 = DateParseUtils.parseToDateTime("2025-07-03 10:20:30");
        LocalDate date = DateParseUtils.tryParseToDate("2025-07-03");
        LocalTime time = DateParseUtils.parseToTime("10:20:30");

        System.out.println(dt2); // 2025-07-03T10:20:30
        System.out.println(date);  // 2025-07-03
        System.out.println(time);  // 10:30

    }
}
