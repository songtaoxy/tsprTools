package com.st.tools.springbootweb.pojo;

import lombok.Data;
import org.slf4j.Logger;

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
}
