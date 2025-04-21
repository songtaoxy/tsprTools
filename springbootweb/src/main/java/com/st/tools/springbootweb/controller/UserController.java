package com.st.tools.springbootweb.controller;

import com.st.tools.springbootweb.model.entity.User;
import com.st.tools.springbootweb.config.UserConfig;
import com.st.tools.springbootweb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

  @GetMapping("/findall")
  public List<User> findAll() {
    return userService.findAll();
  }

  /**
   * 按页查找<p></p>
   *
   *
   * a case of for testing
   * <pre>{@code
   *    curl --location --request  \
   *    POST 'http://localhost:8080/st/user/findPage/100?page=1&pageSize=2' \
   *    --header 'User-Agent: apifox/1.0.0 (https://www.apifox.cn)' \
   *    --header 'Content-Type: application/json' \
   *    --data-raw '{
   *     "v1": "k1",
   *     "v2": "k2"
   *     }'
   *
   * }</pre>
   *
   * @param index
   * @param current
   * @param pageSize
   * @param map
   * @return
   */
  @PostMapping(value = "/findPage/{index}")
  public List<User> findPage(
      @PathVariable int index,
      @RequestParam("page") Integer current,
      @RequestParam("pageSize") Integer pageSize,
      @RequestBody Map map) {

    log.info("index    ===> {}",index);
    log.info("current  ===> {}",current);
    log.info("pageSize ===> {}",pageSize);
    log.info("map      ===> {}",map);


    System.out.println(userConfig.getName());
    return userService.findPage(current, pageSize);
  }

  @GetMapping("/{id}")
  public User getUserById(@PathVariable("id") Integer id) {

   User user =  userService.getUserById(id);
    return user;
  }
}

