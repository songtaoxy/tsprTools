package com.st.modules.serialNumber;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <pre>
 * - testSerialIncrement()：单线程下序号递增。
 * - testSerialMultiThread()：多线程下生成唯一、不重复。
 * - testResetOnNewDay()：模拟跨天，序号能重置（利用反射模拟日期变化，实际运行环境跨天无需此步）
 * </pre>
 */
class DailySerialNoGeneratorTest {

    // 测试单线程生成序号连续
    @Test
    void testSerialIncrement() {
        DailySerialNoGenerator gen = DailySerialNoGenerator.getInstance();
        String first = gen.nextSerial();
        String second = gen.nextSerial();
        Assertions.assertEquals("001", first);
        Assertions.assertEquals("002", second);
    }

    // 测试多线程并发生成序号唯一且不重复
    @Test
    void testSerialMultiThread() throws InterruptedException {
        DailySerialNoGenerator gen = DailySerialNoGenerator.getInstance();
        int threadCount = 20;
        int perThread = 20;
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> allSerials = java.util.Collections.synchronizedSet(new HashSet<>());
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    allSerials.add(gen.nextSerial());
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        Assertions.assertEquals(threadCount * perThread, allSerials.size(), "所有流水号应唯一无重复");
    }

    // 测试跨天重置（通过反射或直接调用重置私有成员实现模拟）
    @Test
    void testResetOnNewDay() throws Exception {
        DailySerialNoGenerator gen = DailySerialNoGenerator.getInstance();

        // 生成当天序号
        String s1 = gen.nextSerial();
        Assertions.assertEquals("001", s1);

        // 强制修改 currentDate 为前一天，模拟新的一天
        java.lang.reflect.Field dateField = DailySerialNoGenerator.class.getDeclaredField("currentDate");
        dateField.setAccessible(true);
        String yesterday = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis() - 24*3600*1000));
        dateField.set(gen, yesterday);

        // 新的一天，序号应重置
        String s2 = gen.nextSerial();
        Assertions.assertEquals("001", s2);
    }

}