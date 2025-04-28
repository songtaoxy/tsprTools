package com.st.tools.common.model.vo;


import com.st.modules.json.jackson.JacksonUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2023/7/13 17:23
 * @version: 1.0
 * @description:
 */
@Data
@Accessors(chain = true)
@Slf4j
public class VO2 {

	public static final String name = "name";
	public  String name2 = "name";


	public static void main(String[] args) {

		VO1 n2 = new VO1().setName2("n2");
		VO2 vo2 = JacksonUtils.convert(n2, VO2.class);
		log.info(vo2.toString());



	}
}
