package com.st.tools.springbootweb.service;



import com.st.tools.springbootweb.pojo.User;

import java.util.List;

public interface UserService {

	public List<User> findAll();

	public List<User> findPage(int current, int pageSize);
}
