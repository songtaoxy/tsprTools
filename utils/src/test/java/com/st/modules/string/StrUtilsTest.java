package com.st.modules.string;

import org.junit.Test;
public class StrUtilsTest {


    @Test
    public void testFormat(){
        String msg = null;

        // 普通用法
        // 用户 Tom 登录，IP=127.0.0.1
        msg = StrUtils.format("用户 {} 登录，IP={}", "Tom", "127.0.0.1");
        System.out.println(msg);

        // 转义
        // 表达式为 {} + 10

        msg = StrUtils.format("表达式为 \\{} + {}", 10);
        System.out.println(msg);

        // 多余占位
        // 输出：1 + 2 = {}
        msg = StrUtils.format("{} + {} = {}", 1, 2);
        System.out.println(msg);

        // 多余参数
        // 输出：1 + 2 = {}
        msg = StrUtils.format("{} + {} = {}", 1, 2,3,5);
        System.out.println(msg);

    }
}