package com.st.tools.modules.demo.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.st.tools.modules.demo.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

  public List<User> listAllUsers();

  @Select("select * from user")
  public List<User> listAllUsers2();
}