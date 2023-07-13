package com.st.tools.springbootweb.pojo.vo;

import com.st.utils.json.gson.GsonUtils;
import lombok.Builder;
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
		VO2 vo2 = GsonUtils.o2o(n2, VO2.class);
		log.info(vo2.toString());



	}
}
