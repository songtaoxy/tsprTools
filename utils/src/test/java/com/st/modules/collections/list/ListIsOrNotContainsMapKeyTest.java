package com.st.modules.collections.list;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ListIsOrNotContainsMapKeyTest {

    @Test
    void isMapKeysContainedInList() {


        List<String> list = Arrays.asList("a", "b", "c", "d");

        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1);
        map1.put("c", 2);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("a", 1);
        map2.put("x", 9);

        System.out.println(ListIsOrNotContainsMapKey.isMapKeysContainedInList(list, map1)); // true
        System.out.println(ListIsOrNotContainsMapKey.isMapKeysContainedInList(list, map2)); // false
    }
}