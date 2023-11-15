package com.st.modules.log2;

import com.alibaba.fastjson.JSONObject;
import com.st.modules.alibaba.fastjson.v1_2_76.FastJsonUtil;
import com.st.modules.alibaba.vos.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author: st
 * @date: 2023/11/8 12:40
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
//@NoArgsConstructor
@Slf4j
public class LogBody {

	private String topic;
	private String des;
	private JSONObject infos_js;
	private Map infos_map;
	private Object infos_obj;
	private String at;

	public LogBody() {
		this.at = LogBody.getDate();
	}

	public static String  getDate() {

		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:SS");
		String timeStr = simpleDateFormat.format(date);
		return timeStr;
	}


	public static void main(String[] args) {
		String topic = "/log/test/this is a test case";
		JSONObject jsonObject = FastJsonUtil.buildJS();
		//jsonObject.put("topic", topic);
		jsonObject.put("zip name", "x.zp");
		jsonObject.put("zip path", "/data/yonyou/nchome/nclogs.log");

		Person person = new Person();
		person.setAge(10);

		LogBody logBody = new LogBody();
		logBody.setTopic(topic);
		logBody.setInfos_js(jsonObject);
		logBody.setInfos_obj(person);
		//log.info(FastJsonUtil.format(logBody));
		System.out.println(FastJsonUtil.format(logBody));


	}
}
