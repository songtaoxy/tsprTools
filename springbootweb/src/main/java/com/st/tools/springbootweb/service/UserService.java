package com.st.tools.springbootweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.st.tools.springbootweb.model.entity.User;

import java.util.List;

/**
 * 为什么要 {@code extends IService<User>}? 使用其中已经定义好的方法. 见 {@code extends ServiceImpl<UserMapper,User>
 * } <br>
 * 具体see:Springboot-v2-尚硅谷-雷丰阳-v202012-vip-latest/066-001-src-数据访问-crud实验-数据列表展示-补录.mp4
 */
public interface UserService extends IService<User> {

  public List<User> findAll();

  public List<User> findPage(int current, int pageSize);

  public User getUserById(Integer id);
}
