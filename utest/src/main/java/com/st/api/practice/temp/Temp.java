package com.st.api.practice.temp;

//import jdk.management.resource.internal.inst.SocketOutputStreamRMHooks;

import java.util.concurrent.CountDownLatch;

/**
 * @author: st
 * @date: 2021/3/11 19:20
 * @version: 1.0
 * @description:
 */
public class Temp {


    public static void main(String[] args) {
        System.out.println("Hello world");

        int count = 2;

        count = sell(count);

        System.out.println(count);

    }

    //    public static synchronized int sell(int count){
    public static int sell(int count) {
        if (count > 0) {
            count--;
        }
        return count;

    }


    public static int add(int a, int b) {

        int c = a + b;

        System.out.println(c);
        return c;


    }

}
