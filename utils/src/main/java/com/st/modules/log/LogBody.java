package com.st.modules.log;

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
//	private JSONObject infos_js;
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
}
