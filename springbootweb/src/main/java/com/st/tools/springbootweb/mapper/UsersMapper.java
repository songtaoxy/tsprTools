package com.st.tools.springbootweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.st.tools.springbootweb.vo.Users;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UsersMapper extends BaseMapper<Users> {
}
