package com.st.tools.springbootweb.controller;

import com.st.tools.springbootweb.pojo.User;
import com.st.tools.springbootweb.pojo.UserConfig;
import com.st.tools.springbootweb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2021/6/22 02:25
 * @version: 1.0
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired private UserService userService;

  @Autowired private UserConfig userConfig;

  @RequestMapping("/findall")
  public List<User> findAll() {
    return userService.findAll();
  }

  @RequestMapping(value = "/findPage/{index}",method = RequestMethod.POST)
  public List<User> findPage(@PathVariable String index,
                             @RequestParam("page")Integer current,
                             @RequestParam("pageSize") Integer pageSize,
                             @RequestBody Map map){


    log.info("index    ===> {}",index);
    log.info("current  ===> {}",current);
    log.info("pageSize ===> {}",pageSize);
    log.info("map      ===> {}",map);


    System.out.println(userConfig.getName());
    return userService.findPage(current, pageSize);
  }
}

