package com.st.modules.serialNumber;

import java.io.*;
        import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * - 其他ref {@code com.st.modules.serialNumber.PersistentFileSerialNumberGenerator}及每日生成序列号.md
 * - 每个系统每天独立递增、每天重置”的版本，每个系统通过systag参数区分，每天每个系统独立一套序号，全局单例、线程安全、持久化本地文件（可选也可扩展为数据库/Redis/多机同步）
 * - 支持“每个系统，每天独立序号”，每天重置，线程安全，持久化到本地
 * - systemId：唯一标识系统，可以是系统名、编码、模块名等，每个系统每天的序号互不影响。
 * - 文件持久化，重启后序号不丢失；跨天自动重置。
 * - 线程安全，支持并发（全局锁/细粒度可优化）。
 * - SystemSerial序列化时只保存当天、当前序号。
 * - 扩展持久化方式：如需更高一致性，建议持久化到数据库/Redis。
 * - nextSerial超出999时报错，可改为回绕或自定义处理
 *
 * </pre>
 * Usage
 * <pre>
 *     {@code
 *      SerialNumberManager manager = SerialNumberManager.getInstance();
 *
 *         System.out.println(manager.nextSerial("AP")); // AP系统，001
 *         System.out.println(manager.nextSerial("AP")); // AP系统，002
 *         System.out.println(manager.nextSerial("AR")); // AR系统，001
 *         System.out.println(manager.nextSerial("AP")); // AP系统，003
 *         System.out.println(manager.nextSerial("AR")); // AR系统，002
 *     }
 *     }
 * </pre>
 *
 *
 */
public class ModuleSerialNumberGeneratorPersistFile implements Serializable {

    // 持久化文件名
    private static final String DATA_FILE = "system_serial_numbers.dat";

    // 单例
    private static volatile ModuleSerialNumberGeneratorPersistFile instance;

    // 存储每个系统的序号状态（含当天、序号）
    private final Map<String, SystemSerial> systemSerials = new ConcurrentHashMap<>();
    // 内部锁
    private final Object lock = new Object();

    // 内部类: 记录每个系统的当天与计数器
    private static class SystemSerial implements Serializable {
        LocalDate currentDate;
        AtomicInteger counter;
        SystemSerial(LocalDate date, int value) {
            this.currentDate = date;
            this.counter = new AtomicInteger(value);
        }
    }

    private ModuleSerialNumberGeneratorPersistFile() {
        load();
    }

    public static ModuleSerialNumberGeneratorPersistFile getInstance() {
        if (instance == null) {
            synchronized (ModuleSerialNumberGeneratorPersistFile.class) {
                if (instance == null) {
                    instance = new ModuleSerialNumberGeneratorPersistFile();
                }
            }
        }
        return instance;
    }

    /**
     * 获取下一个序号，每个系统每天独立一套
     * @param systemId 系统编码或唯一标识
     * @return 序号"001"~"999"
     */
    public String nextSerial(String systemId) {
        synchronized (lock) {
            LocalDate today = LocalDate.now();
            SystemSerial sys = systemSerials.get(systemId);
            if (sys == null || !today.equals(sys.currentDate)) {
                // 新系统或新的一天，重置为001
                sys = new SystemSerial(today, 0);
                systemSerials.put(systemId, sys);
            }
            int val = sys.counter.incrementAndGet();
            if (val > 999) throw new IllegalStateException(systemId + "当天序号已达上限999");
            save();
            return String.format("%03d", val);
        }
    }

    // 加载所有系统的序号状态
    private void load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof Map) {
                Map<?, ?> saved = (Map<?, ?>) obj;
                for (Map.Entry<?, ?> entry : saved.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof SystemSerial) {
                        SystemSerial old = (SystemSerial) entry.getValue();
                        // 注意：反序列化AtomicInteger时需重建
                        systemSerials.put((String) entry.getKey(), new SystemSerial(
                                old.currentDate,
                                old.counter == null ? 0 : old.counter.get()
                        ));
                    }
                }
            }
        } catch (Exception ignore) {}
    }

    // 保存所有系统序号状态
    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(systemSerials);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
