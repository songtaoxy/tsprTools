package com.st.api.practice.collectionstream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2021/6/29 15:07
 * @version: 1.0
 * @description:
 */
@Slf4j
public class CollectionStream {

  @Test
  @DisplayName("stream for each")
  public void foreach() {

    Random random = new Random();
    random.ints().limit(10).forEach(System.out::println);
  }

  @Test
  public void map() {
    List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
    // 获取对应的平方数
    List<Integer> squaresList =
        numbers.stream().map(i -> i * i).distinct().collect(Collectors.toList());
  }
}
