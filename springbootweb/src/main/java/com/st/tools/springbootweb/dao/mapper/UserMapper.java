package com.st.tools.springbootweb.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.st.tools.springbootweb.model.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

  public List<User> listAllUsers();

  @Select("select * from user")
  public List<User> listAllUsers2();
}