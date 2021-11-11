package com.st.api.practice.sort;

import com.st.api.practice.pojo.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: st
 * @date: 2021/6/30 14:35
 * @version: 1.0
 * @description:
 */
@Slf4j
public class CopyOnWriteArrayListTest {

  @Test
  public void compareString() {
    String s1 = "this";
    String s2 = "that";
    String s3 = "good";

    int i = s1.compareToIgnoreCase(s2);
    log.info(String.valueOf(i));


    List<String> strings = Arrays.asList("abc", "", "bc","abad", "efg", "abcd","", "jkl");
    List<String> filtered = strings.stream().sorted().collect(Collectors.toList());

    log.info(String.valueOf(filtered) );


    Person[] pArr =
            new Person[] {
                    new Person("003", LocalDate.of(2016, 9, 1)),
                    new Person("001", LocalDate.of(2016, 2, 1)),
                    new Person("002", LocalDate.of(2016, 3, 1)),
                    new Person("004", LocalDate.of(2016, 12, 1))
            };

    Arrays.sort(
            pArr,
            (Person a, Person b) -> {
              return a.getName().compareTo(b.getName());
            });

    System.out.println(Arrays.asList(pArr));


    ArrayList<Person> personArrayList = Stream.of(pArr).collect(Collectors.toCollection(ArrayList::new));

    List<Person> collect =
        personArrayList.stream()
            .sorted(
                (o1, o2) -> {
                  return o1.getName().compareTo(o2.getName());
                })
            .collect(Collectors.toList());

    log.info(String.valueOf(collect));
  }




}
