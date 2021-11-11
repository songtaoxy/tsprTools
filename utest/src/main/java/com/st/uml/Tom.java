package com.st.uml;

import lombok.experimental.PackagePrivate;
import org.mockito.exceptions.misusing.FriendlyReminderException;

/**
 * @author: st
 * @date: 2021/9/29 17:50
 * @version: 1.0
 * @description:
 */

/**
 * UML 类图之间的关系
 */
// parenet: 继承
// person: 实现
public class Tom extends Parent implements Person {

  private static String nameFull;
  private String name;
  public String age;

  // 继承
  private Parent parent;
  // 关联: 成员属性, 非setter, 非contructor; 一般方法赋值
  private Friend friend;

  // 聚合: setter
  private Car car;

  // 合成: new
  private Hand hands = new Hand();
  // 合成: constructor
  private Leg leg;

  // constructor
  public Tom(Leg leg) {
    this.leg = leg;
  }

  // setter
  public void setCar(Car car) {
    this.car = car;
  }
  // 关联: 一般方法赋值
  private void makeFrined(Person person) {
    friend = (Friend) person;
  }

  // 依赖: 返回值
  public Money makemoney() {
    return new Money();
  }
}
