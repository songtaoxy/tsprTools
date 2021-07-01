package com.st.api.practice.compare;

import com.st.api.practice.pojo.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author: st
 * @date: 2021/6/29 19:20
 * @version: 1.0
 * @description:测试lambda表达式以及方法引用
 */
public class PersonAgeComparator implements Comparator<Person> {
  @Override
  public int compare(Person p1, Person p2) {
    return p1.getBirthday().compareTo(p2.getBirthday());
  }

  @Test
  public void compare_lambda_methodref() {

    Person[] pArr =
        new Person[] {
          new Person("003", LocalDate.of(2016, 9, 1)),
          new Person("001", LocalDate.of(2016, 2, 1)),
          new Person("002", LocalDate.of(2016, 3, 1)),
          new Person("004", LocalDate.of(2016, 12, 1))
        };

    // 传统写法
    Arrays.sort(pArr, new PersonAgeComparator());
    System.out.println(Arrays.asList(pArr));

    // 匿名内部类
    Arrays.sort(
        pArr,
        new Comparator<Person>() {
          @Override
          public int compare(Person o1, Person o2) {
            return o1.getBirthday().compareTo(o2.getBirthday());
          }
        });
    System.out.println(Arrays.asList(pArr));

    // lambda: 未调用已经存在的方法, 直接传入"实现"
    Arrays.sort(
        pArr,
        (Person a, Person b) -> {
          return a.getBirthday().compareTo(b.getBirthday());
        });
    System.out.println(Arrays.asList(pArr));

    // lambda: 调用已经存在的方法. 将已经存在的方法, 当做"实现"
    Arrays.sort(pArr, (a, b) -> Person.compareByAge(a, b));
    System.out.println(Arrays.asList(pArr));

    // lambda: 方法引用
    Arrays.sort(pArr, Person::compareByAge);
    System.out.println(Arrays.asList(pArr));

    // result
    String result =
              "[001, 002, 003, 004]\n"
            + "[001, 002, 003, 004]\n"
            + "[001, 002, 003, 004]\n"
            + "[001, 002, 003, 004]\n"
            + "[001, 002, 003, 004]";
  }
}
