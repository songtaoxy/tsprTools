package com.st.tools.springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.st.tools.springbootweb.mapper.UserMapper;
import com.st.tools.springbootweb.pojo.User;
import com.st.tools.springbootweb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.List;

/**
 * @author: st
 * @date: 2021/6/22 02:14
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class UserServiceImp implements UserService {

  @Autowired private UserMapper userMapper;

  public List<User> findAll() {
    // 查询所有
    return userMapper.selectList(new LambdaQueryWrapper<>());
  }

  public List<User> findPage(int current, int pageSize) {

    Page<User> page = new Page<>(current, pageSize);

    Page<User> userPage = userMapper.selectPage(page, new LambdaQueryWrapper<>());

    log.info("total:{}", userPage.getTotal());
    log.info("pageSize:{}", userPage.getPages());

    return userPage.getRecords();
  }
}
