package com.st.modules.timing.stopwatch;

import com.google.common.base.Stopwatch;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Stopwatch 工具类
 * 功能:
 * 1. 记录开始时间与截止时间 (支持格式化字符串)
 * 2. 提供耗时统计（毫秒、秒、纳秒）
 * 3. 可多次启动与停止
 * </pre>
 *
 * Usage
 * <pre>
 *     {@code
 *     public static void main(String[] args) throws InterruptedException {
 *         StopwatchUtil sw = StopwatchUtil.start();
 *
 *         Thread.sleep(1250); // 模拟耗时任务
 *
 *         sw.stop();
 *
 *         System.out.println("开始时间: " + sw.getStartTimeFormatted());
 *         System.out.println("截止时间: " + sw.getEndTimeFormatted());
 *         System.out.println("耗时(ms): " + sw.elapsedMillis());
 *
 *         // output
 *         // 开始时间: 2025-08-28 13:26:23 719
 *         // 截止时间: 2025-08-28 13:26:24 986
 *         // 耗时(ms): 1255
 *         // 耗时(s): 1
 *     }
 *     }
 * </pre>
 */
public class StopwatchUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DEFAULT_PATTERN);

    private final Stopwatch stopwatch;
    private long startTimeMillis;
    private long endTimeMillis;

    private StopwatchUtil() {
        this.stopwatch = Stopwatch.createUnstarted();
    }

    /** 开始计时 */
    public static StopwatchUtil start() {
        StopwatchUtil util = new StopwatchUtil();
        util.startTimeMillis = System.currentTimeMillis();
        util.stopwatch.start();
        return util;
    }

    /** 停止计时 */
    public void stop() {
        this.stopwatch.stop();
        this.endTimeMillis = System.currentTimeMillis();
    }

    /** 获取开始时间戳 (毫秒) */
    public long getStartTime() {
        return startTimeMillis;
    }

    /** 获取截止时间戳 (毫秒) */
    public long getEndTime() {
        return endTimeMillis;
    }

    /** 获取格式化的开始时间 */
    public String getStartTimeFormatted() {
        return FORMATTER.format(new Date(startTimeMillis));
    }

    /** 获取格式化的截止时间 */
    public String getEndTimeFormatted() {
        return FORMATTER.format(new Date(endTimeMillis));
    }

    /** 获取耗时 (毫秒) */
    public long elapsedMillis() {
        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }

    /** 获取耗时 (秒) */
    public long elapsedSeconds() {
        return stopwatch.elapsed(TimeUnit.SECONDS);
    }

    /** 获取耗时 (纳秒) */
    public long elapsedNanos() {
        return stopwatch.elapsed(TimeUnit.NANOSECONDS);
    }



    public static void main(String[] args) throws InterruptedException {
        StopwatchUtil sw = StopwatchUtil.start();

        Thread.sleep(1250); // 模拟耗时任务

        sw.stop();

        System.out.println("开始时间: " + sw.getStartTimeFormatted());
        System.out.println("截止时间: " + sw.getEndTimeFormatted());
        System.out.println("耗时(ms): " + sw.elapsedMillis());
    }

}
