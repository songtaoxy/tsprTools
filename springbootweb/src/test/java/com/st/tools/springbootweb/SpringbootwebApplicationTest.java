package com.st.tools.springbootweb;

import com.oracle.tools.packager.Log;
import com.st.tools.springbootweb.mapper.UsersMapper;
import com.st.tools.springbootweb.vo.Users;
import com.st.utils.log.LogUtils;
import jdk.nashorn.api.scripting.ScriptUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringbootwebApplicationTest {

  @Autowired DataSource dataSource;
  @Autowired UsersMapper usersMapper;

  @Test
  void testMytatisPlus() {

    Users user = usersMapper.selectById(3L);
    List<Users> users = usersMapper.selectList(null);

    LogUtils.foal(dataSource.getClass(), "datasource type");
    LogUtils.foal(user, "");
    LogUtils.foal(users, "");
  }
}