package com.st.tools.springbootweb.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.st.tools.springbootweb.pojo.Person;
import com.st.tools.springbootweb.pojo.User2.User2;
import com.st.tools.springbootweb.pojo.mapper.User2Mapper;
import com.st.tools.springbootweb.pojo.service.User2Service;
import com.st.tools.springbootweb.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class HelloWorld {
  static String template;

  @Autowired HelloService helloService;


  @Autowired
  User2Service user2Service;

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



    Page<User2> user2Page = new Page<>(0, 2);
    QueryWrapper<User2> user2QueryWrapper = new QueryWrapper<User2>();
    //user2QueryWrapper.eq("name", "Jone");


    user2QueryWrapper.gt("u.id", 1);
    //user2QueryWrapper.isNotNull("u.name");
    IPage<Map<String,Object>> stringIPage = user2Service.queryByPage(user2Page, user2QueryWrapper,"null");
    //IPage<String> stringIPage = user2Service.queryByPage(user2Page,null);
    System.out.println(stringIPage.getTotal());
    System.out.println(stringIPage.getPages());
    List<Map<String,Object>> records = stringIPage.getRecords();
    records.forEach(System.out::println);
    return hello;
  }
}

