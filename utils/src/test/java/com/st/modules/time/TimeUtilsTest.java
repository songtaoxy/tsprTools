package com.st.modules.time;

import org.junit.jupiter.api.Test;

class TimeUtilsTest {

    @Test
    void timeStr() {

        String s = TimeUtils.time2Str();
        System.out.println(s);
    }
}