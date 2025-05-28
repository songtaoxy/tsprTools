package com.st.modules.serialNumber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <pre>
 * - 持久化版本, 见:com.st.modules.serialNumber.PersistentFileSerialNumberGenerator and 每日生成序列号.md
 * - 线程安全、全局单例、每天重置、每次自增
 * - 全局单例：getInstance() 确保单 JVM 唯一实例。
 * - synchronized 保证多线程安全，每天自动重置。
 * - 重启后序号不会连续，如需持久化需用 DB/Redis; 重启后, 从001重新开始。
 * - 超过 999 可自行扩展（如 %04d）。
 * - 不依赖数据库，部署/测试环境简单可用
 * </pre>
 *
 * <pre>
 *  - 每个系统（如 appId、systemId 等），每天都独立从 001 开始计数。
 *  - 每天零点重置，不同系统间序号互不影响
 *  - 线程安全
 * </pre>
 */
public class DailySystemSerialNoGenerator {

    // key: systemId，value: SystemSerial
    private final Map<String, SystemSerial> systemSerials = new ConcurrentHashMap<>();

    // 单例
    private static final DailySystemSerialNoGenerator INSTANCE = new DailySystemSerialNoGenerator();
    public static DailySystemSerialNoGenerator getInstance() {
        return INSTANCE;
    }

    // 生成下一个序号
    public String nextSerial(String systemId) {
        if (systemId == null || systemId.trim().isEmpty()) systemId = "default";
        SystemSerial sys = systemSerials.computeIfAbsent(systemId, id -> new SystemSerial());
        return sys.nextSerial();
    }

    // 内部类：单系统每日流水号
    private static class SystemSerial {
        private String currentDate = today();
        private AtomicInteger serialNo = new AtomicInteger(0);

        public synchronized String nextSerial() {
            String now = today();
            if (!now.equals(currentDate)) {
                currentDate = now;
                serialNo.set(0);
            }
            int val = serialNo.incrementAndGet();
            return String.format("%03d", val);
        }
    }

    private static String today() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}

