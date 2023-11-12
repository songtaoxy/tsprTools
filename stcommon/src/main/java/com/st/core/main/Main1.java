package com.st.core.main;

import com.st.core.util.StrUtil;

public class Main1 {

    public static void main(String[] args) {
        String format = StrUtil.format("we are {}, and are you {}", "goo", "ok");

        System.out.println(format);

    }
}
