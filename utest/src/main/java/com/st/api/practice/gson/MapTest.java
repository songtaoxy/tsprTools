package com.st.api.practice.gson;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/4/15 09:36
 * @version: 1.0
 * @description:
 */
@Slf4j
public class MapTest {
    public static void main(String[] args) {
        Person person = new Person("song");
        Map resultMap = new HashMap<>();
        resultMap.put("k1", "v1");

        resultMap.put("p", person);

        log.info("resultMap is:{}", resultMap);
    }
}

class Person {
    private String name;
    public Person(String name) {
        this.name = name;
    }
}