package com.st.utils.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: st
 * @date: 2021/11/6 15:09
 * @version: 1.0
 * @description:
 */
@Slf4j
public class BasicUtils {


	public static void formatObjAndLogging(Object obj, String infoTips) {
		log.info("\n" +
						"================================== start =====================================\n" +
						"- [Type    ]:"+obj.getClass().getName()+"\n"+
						"- [messsage]:"+infoTips+"\n" +
						"- [content ]:\n"+
						"------------------------------------------------------------------------------\n" +
						"{}\n"+
						"===================================  end  ====================================\n"
				, JSON.toJSONString(obj, true));
	}
}
