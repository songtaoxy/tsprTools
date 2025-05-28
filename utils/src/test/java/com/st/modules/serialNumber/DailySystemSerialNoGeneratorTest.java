package com.st.modules.serialNumber;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DailySystemSerialNoGeneratorTest {


    @Test
    void testDifferentSystemsIndependentSerial() {
        DailySystemSerialNoGenerator gen = DailySystemSerialNoGenerator.getInstance();

        String s1a = gen.nextSerial("A");
        String s1b = gen.nextSerial("A");
        String s2a = gen.nextSerial("B");
        String s2b = gen.nextSerial("B");

        Assertions.assertEquals("001", s1a);
        Assertions.assertEquals("002", s1b);
        Assertions.assertEquals("001", s2a);
        Assertions.assertEquals("002", s2b);
    }

    @Test
    public void testSystemResetOnNewDay() throws Exception {
        DailySystemSerialNoGenerator gen = DailySystemSerialNoGenerator.getInstance();
        String sid = "X";
        String n1 = gen.nextSerial(sid);
        assertEquals("001", n1);

        // 反射获取systemSerials字段
        java.lang.reflect.Field mapField = DailySystemSerialNoGenerator.class.getDeclaredField("systemSerials");
        mapField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) mapField.get(gen);
        Object sysObj = map.get(sid);

        // 修改currentDate为昨天
        java.lang.reflect.Field dateField = sysObj.getClass().getDeclaredField("currentDate");
        dateField.setAccessible(true);
        String yesterday = new java.text.SimpleDateFormat("yyyyMMdd").format(
                new java.util.Date(System.currentTimeMillis() - 86400_000));
        dateField.set(sysObj, yesterday);

        String n2 = gen.nextSerial(sid);
        assertEquals("001", n2);
    }

}