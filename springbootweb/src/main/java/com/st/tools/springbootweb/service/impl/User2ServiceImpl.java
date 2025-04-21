package com.st.tools.springbootweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.st.tools.springbootweb.model.entity.User2;
import com.st.tools.springbootweb.dao.mapper.User2Mapper;
import com.st.tools.springbootweb.service.User2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
* @author songtao
* @description 针对表【user2】的数据库操作Service实现
* @createDate 2022-05-06 01:46:13
*/
@Service
public class User2ServiceImpl extends ServiceImpl<User2Mapper, User2>
    implements User2Service {

	@Autowired User2Mapper user2Mapper;

	@Override
	public IPage<Map<String,Object>> queryByPage(IPage<User2> page, Wrapper ew, String flag) {
		return user2Mapper.queryByPage(page,ew,flag );
	}


}




