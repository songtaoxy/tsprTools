package com.st.modules.serialNumber;

import java.io.*;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <pre>
 *     Prod:
 *     - 定时任务, 每次执行定时任务时, 生成一个序号, 从001开始, 每执行一次+1; 每天重置为001
 *     - 多线程环境安全（如并发触发定时任务）
 *     - 边界情况（如重启、跨天、高并发）健壮处理
 *
 *     结论: 全局单例、线程安全、重启后序号连续、跨天重置
 *
 *     实现方案:
 *     - 使用AtomicInteger保证序号递增线程安全。
 *     - 使用LocalDate判断日期是否变更，跨天自动重置。
 *     - 用synchronized或ReentrantLock保证重置与递增的原子性。
 *
 *     设计要点:
 *     - 全局单例：适用于Spring项目（Bean）或普通项目（static单例），线程安全。
 *     - 持久化：启动时加载、每次变更后保存序号到本地文件，保证重启连续。
 *     - 自动重置：跨天重置，持久化文件也重置。
 *     - 线程安全：所有关键操作加锁，防止并发竞态
 *
 *     边界说明:
 *     <u>线程安全</u>：所有操作都加锁，保证跨天与递增无竞态。
 *     <u>跨天自动重置</u>：通过判断日期变更自动重置序号。
 *     <u>高并发</u>：建议作为全局单例（如 Spring bean 或 static），不要频繁 new。
 *     <u>序号上限</u>：如超过 999 可自定义抛异常或回绕。
 *     <u>重启问题</u>：如需重启后序号连续,进行持久化. 持久化位置：当前工作目录，如需多实例共用建议存在共享存储或切换为DB/Redis实现。
 *     高可用/分布式：如需多实例分布式全局唯一，可扩展为Redis原子递增。
 *     文件损坏：异常时自动重置（不影响业务），如有强一致需求需额外容灾机制。
 *     Spring单例：可注册为@Component，保证全局单例与线程安全。
 *
 * </pre>
 * Usage
 * <pre>
 * {@code
 * // 单例用法
 * String serial = DailySerialNumberGenerator.getInstance().nextSerial();
 * // serial 例："001", "002", "003"...}
 * </pre>
 */


public class PersistentFileSerialNumberGenerator implements Serializable {
    private static final String DATA_FILE = "serial_number.dat";

    private LocalDate currentDate;
    private AtomicInteger counter;

    // 单例
    private static PersistentFileSerialNumberGenerator instance;

    private final Object lock = new Object();

    private PersistentFileSerialNumberGenerator() {
        // 加载历史数据
        load();
    }

    public static PersistentFileSerialNumberGenerator getInstance() {
        if (instance == null) {
            synchronized (PersistentFileSerialNumberGenerator.class) {
                if (instance == null) {
                    instance = new PersistentFileSerialNumberGenerator();
                }
            }
        }
        return instance;
    }

    /**
     * 获取下一个序号（跨天自动重置，重启连续）
     * @return "001" ~ "999"
     */
    public String nextSerial() {
        synchronized (lock) {
            LocalDate today = LocalDate.now();
            if (!today.equals(currentDate)) {
                counter.set(0);
                currentDate = today;
            }
            int val = counter.incrementAndGet();
            if (val > 999) {
                throw new IllegalStateException("当天序号已达上限999");
            }
            // 持久化
            save();
            return String.format("%03d", val);
        }
    }

    // 加载持久化数据
    private void load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            currentDate = LocalDate.now();
            counter = new AtomicInteger(0);
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            currentDate = (LocalDate) in.readObject();
            counter = new AtomicInteger(in.readInt());
        } catch (Exception e) {
            // 读取异常时，重置为新的一天
            currentDate = LocalDate.now();
            counter = new AtomicInteger(0);
        }
    }

    // 保存持久化数据
    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(currentDate);
            out.writeInt(counter.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Spring Bean 用法
     */
   /* @Component
    public class DailySerialService {
        private final PersistentSerialNumberGenerator generator = PersistentSerialNumberGenerator.getInstance();

        public String getNextSerial() {
            return generator.nextSerial();
        }
    }*/

}
