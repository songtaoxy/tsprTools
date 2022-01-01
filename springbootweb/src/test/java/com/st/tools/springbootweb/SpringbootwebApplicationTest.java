package com.st.tools.springbootweb;

import com.st.tools.springbootweb.mapper.UserMapper;
import com.st.tools.springbootweb.pojo.User;
import com.st.utils.log.LogUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringbootwebApplicationTest {

  @Autowired DataSource dataSource;
  @Autowired UserMapper usersMapper;

  @Test
  void testMytatisPlus() {

    User user = usersMapper.selectById(3L);
    List<User> users = usersMapper.selectList(null);

    LogUtils.foal(dataSource.getClass(), "datasource type");
    LogUtils.foal(user, "");
    LogUtils.foal(users, "");
  }
}
