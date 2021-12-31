package com.st.tools.springbootweb;

import com.st.tools.springbootweb.mapper.UsersMapper;
import com.st.tools.springbootweb.vo.Users;
import com.st.utils.log.LogUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringbootwebApplicationTest {

  @Autowired UsersMapper usersMapper;

	@Test
	void testMytatisPlus(){

		Users users = usersMapper.selectById(3L);

		LogUtils.foal(users,"");
		
	}

}