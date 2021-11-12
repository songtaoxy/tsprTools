package com.st.tools.springbootweb.controller;

import com.st.tools.springbootweb.service.HelloService;
import com.st.utils.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class HelloWorld {
  static String template;

  @Autowired HelloService helloService;

  Person p = null;

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> helloWorld() {

    Map<String, Object> hello = new HashMap<>();
    hello.put("hello", "helloworld");
    hello.put("hello2", "helloworld");

    // log.info("hello world");
    // LogUtils.formatObjAndLogging(hello,"result response");

    System.out.println("controller:"+hello);

    Person<Object> objectPerson = new Person<>();

    return hello;
  }
}

class Person<T> {

  // 使用T类型定义变量
  private T info;

  // 使用T类型定义一般方法
  public T getInfo() {

    return info;
  }

  public void setInfo(T info) {

    this.info = info;
  } // 使用T类型定义构造器

  public Person() {}

  public Person(T info) {

    this.info = info;
  }
}
