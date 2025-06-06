package com.st.tools.modules.demo.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.st.tools.modules.demo.model.entity.User2;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author songtao
* @description 针对表【user2】的数据库操作Mapper
* @createDate 2022-05-06 01:46:13
* @Entity pojo.User2.User2
*/
@Mapper
public interface User2Mapper extends  BaseMapper<User2> {

	@MapKey("id")
	public IPage<Map<String, Object>> queryByPage(@Param("page") IPage<User2> page, @Param(Constants.WRAPPER) Wrapper queryWrapper, @Param("flag") String flag);



}




