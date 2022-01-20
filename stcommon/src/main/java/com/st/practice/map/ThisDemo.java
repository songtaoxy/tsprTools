package com.st.practice.map;

import com.st.utils.string.StringUtils;
import lombok.ToString;

/**
 * @author: st
 * @date: 2022/1/20 19:29
 * @version: 1.0
 * @description:
 */
public class ThisDemo {

  String name;

  public static void main(String[] args) throws InstantiationException, IllegalAccessException {
    ThisDemo thisDemo = new ThisDemo(); //
    System.out.println(thisDemo); //1   println中, 自动调用对象的toString方法
    System.out.println("1" == "1");// true
    System.out.println("1".equals("1")); //true
    System.out.println(thisDemo.equals("1")); // false  thisDemo是一个对象, 类型是 ThisDemo, 不可能和一个字符串相等
    System.out.println(thisDemo.toString().equals("1")); //true
    thisDemo.m2();
  }

  @Override
  public String toString() {
    return 1 + "";
  }

  public void m2() throws InstantiationException, IllegalAccessException {

    // 类, 即class是有名字的. 对象是没有名字的
    System.out.println(this.getClass().getName()); // com.st.practice.map.ThisDemo
    System.out.println(this.getClass().getSimpleName());// ThisDemo
    System.out.println(this.getClass().newInstance());// 1

    System.out.println("this:" + this); //1
  }
}
