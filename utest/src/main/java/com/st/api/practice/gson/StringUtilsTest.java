package com.st.api.practice.gson;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * @author: st
 * @date: 2021/4/8 16:38
 * @version: 1.0
 * @description:
 */

@Slf4j
public class StringUtilsTest {


    public static void main(String[] args) {
//        m1();
//        m2_while();
        ClassLoader classLoader = new String().getClass().getClassLoader();
        log.info("the classLoader is:{}", classLoader);

        ClassLoader classLoader_2 = new StringUtilsTest().getClass().getClassLoader();
        log.info("the classLoader is:{}", classLoader_2);
    }

    public static void m1() {

        String s = "a.b.c";

        boolean contains = StringUtils.contains(s, ".");
        log.info("contains: {}", contains);


        String[] split = StringUtils.split(s, ".");
        log.info(String.valueOf(split));
        for (int i = 0; i < split.length; i++) {

            log.info(split[i]);

        }


    }

    public static void m2_while() {

        String s = "a.b.c";

        String[] split = s.split("\\.");

        log.info("length is:{}", split.length);
        String s1 = null;


        if (s.contains(".")) {
            int i = 0;
            while (i < split.length - 1) {

                log.info("i is:{}", String.valueOf(i));

                i++;

            }


            s1 = split[i];

            log.info("s1 is:{}", s1);
        }


    }



}
