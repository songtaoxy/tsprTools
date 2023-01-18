package com.st.tools.springbootweb.pojo.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.IService;
import com.st.tools.springbootweb.pojo.User2.User2;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author songtao
* @description 针对表【user2】的数据库操作Service
* @createDate 2022-05-06 01:46:13
*/
public interface User2Service extends IService<User2> {
	public IPage<Map<String,Object>> queryByPage(IPage<User2> page, Wrapper queryWrapper,String flag);
}
