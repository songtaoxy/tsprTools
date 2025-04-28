package com.st.tools.modules.demo.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author: st
 * @date: 2021/6/22 02:10
 * @version: 1.0
 * @description:
 */
@Data
public class User {
  private Long id;
  private String name;
  private String email;
  private Integer age;

  private String userFriends;

  @TableField(exist = false)
  private String fa;

  // 静态属性不会映射到表中.
  @TableField(exist = false)
  private static String fb;
  @TableField(exist = false)
  private static final String fc = null;
}
