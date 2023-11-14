package com.st.modules.hutool.v4_6_8.main;

import com.st.modules.hutool.v4_6_8.util.StrUtil;

public class Main1 {

    public static void main(String[] args) {
        String format = StrUtil.format("we are {}, and are you {}", "好", "ok");

        System.out.println(format);

    }
}
