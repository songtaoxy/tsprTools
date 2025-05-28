package com.st.modules.serialNumber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <pre>
 * - 持久化版本, 见:com.st.modules.serialNumber.PersistentFileSerialNumberGenerator and 每日生成序列号.md
 * - 线程安全、全局单例、每天重置、每次自增
 * - 全局单例：getInstance() 确保单 JVM 唯一实例。
 * - synchronized 保证多线程安全，每天自动重置。
 * - 重启后序号不会连续，如需持久化需用 DB/Redis。
 * - 超过 999 可自行扩展（如 %04d）。
 * - 不依赖数据库，部署/测试环境简单可用
 *    </pre>
 *
 *     Usage( unit test(+))
 *     <pre>
 *     {@code
 *     public static void main(String[] args) throws InterruptedException {
 *         DailySerialNoGenerator gen = DailySerialNoGenerator.getInstance();
 *         // 多线程模拟
 *         for (int i = 0; i < 5; i++) {
 *             new Thread(() -> {
 *                 for (int j = 0; j < 10; j++) {
 *                     System.out.println(Thread.currentThread().getName() + " -> " + gen.nextSerial());
 *                 }
 *             }).start();
 *         }
 *     }
 *     }
 * </pre>
 */
public class DailySerialNoGenerator {
    // 全局单例
    private static final DailySerialNoGenerator INSTANCE = new DailySerialNoGenerator();

    // 当前日期（yyyyMMdd）
    private volatile String currentDate = getToday();
    // 当天流水号
    private final AtomicInteger counter = new AtomicInteger(0);

    private DailySerialNoGenerator() {}

    public static DailySerialNoGenerator getInstance() {
        return INSTANCE;
    }

    // 获取当天序号，自动重置，返回三位（001、002...）
    public synchronized String nextSerial() {
        String today = getToday();
        if (!today.equals(currentDate)) {
            // 新的一天，重置序号
            currentDate = today;
            counter.set(0);
        }
        int no = counter.incrementAndGet();
        // 超过999可扩展处理
        return String.format("%03d", no);
    }

    // 获取当前日期字符串（yyyyMMdd）
    private String getToday() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }


}

