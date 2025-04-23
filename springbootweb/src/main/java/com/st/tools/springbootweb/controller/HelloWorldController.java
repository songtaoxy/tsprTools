package com.st.tools.springbootweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.st.modules.json.jackson.JacksonUtils;
import com.st.tools.springbootweb.pojo.others.Person;
import com.st.tools.springbootweb.model.entity.User2;
import com.st.tools.springbootweb.response.Response;
import com.st.tools.springbootweb.response.Result;
import com.st.tools.springbootweb.service.User2Service;
import com.st.tools.springbootweb.service.HelloService;
import com.st.tools.springbootweb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HelloWorldController {
  static String template;

  @Autowired HelloService helloService;
  @Autowired UserService userService;


  @Autowired
  User2Service user2Service;

  Person p = null;

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public  Response<Result> helloWorld() {

//    System.out.println(1/0);
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

    Result build = Result.build(hello.toString());
    return Response.ok(build);
  }


  @PostMapping (value = "/hello2")
  public Map<String, Object> helloWorld2() {
    Map map  = new HashMap<String,String >();
    map.put("h","h2");
    return map;
  }
}

