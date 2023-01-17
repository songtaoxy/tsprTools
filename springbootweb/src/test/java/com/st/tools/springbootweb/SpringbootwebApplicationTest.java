package com.st.tools.springbootweb;

import com.st.tools.springbootweb.mapper.UserMapper;
import com.st.tools.springbootweb.pojo.User;
import com.st.utils.log.LogUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringbootwebApplicationTest {
/*
  @Autowired DataSource dataSource;
  @Autowired UserMapper usersMapper;

  @Test
  @DisplayName("查询所有: 通过mybatis中已经已经预置的接口")
  void testMytatisPlus() {

    User user = usersMapper.selectById(3L);
    List<User> users = usersMapper.selectList(null);

    LogUtils.foal(dataSource.getClass(), "datasource type");
    LogUtils.foal(user, "");
    LogUtils.foal(users, "");
  }

  @Test
  @DisplayName("查询所有: 通过mpper配置文件中的sql")
  void testListAllUsers() {
    List<User> users = usersMapper.listAllUsers();
    LogUtils.foal(users,"");
  }


  @Test
  @DisplayName("查询所有: 通过注解中的sql")
  void testListAllUsers2() {
    List<User> users = usersMapper.listAllUsers();
    LogUtils.foal(users,"");
  }*/
}
