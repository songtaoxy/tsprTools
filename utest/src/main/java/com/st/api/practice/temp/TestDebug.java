package com.st.api.practice.temp;

import java.util.concurrent.TimeUnit;

/**
 * @author: st
 * @date: 2021/3/23 18:58
 * @version: 1.0
 * @description:
 */
public class TestDebug {

    /**
     * 如何运行代码?
     * 1, 不打断点. 中间会睡5秒
     * 2, debugg模式运行
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("测试debug， 不打任何断点");
        for (int i = 0; i <= 10; i++) {
            TimeUnit.SECONDS.sleep(5);
            System.out.println(i);

        }
        System.out.println("end");
    }
}
