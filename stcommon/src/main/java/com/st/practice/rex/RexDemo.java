package com.st.practice.rex;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Var;
import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: st
 * @date: 2023/1/10 12:49
 * @version: 1.0
 * @description:
 */
@Slf4j
public class RexDemo {

	public static void main(String[] args) {

		String prefix = "mogo/rss/up/compute/config/ack/";
		String topic = "mogo/rss/up/compute/config/ack/123234543_124_45_8_10";
		boolean b = validPrefix(prefix, topic);
		if (b) {

			System.out.println(b);
		}

		//String s = topicSpliter(topic, topic);
		String s = topicSpliter(topic, "end");
		//String s = topicSpliter(topic, "0");
		//String s = topicSpliter(topic, "x");
		log.info(".........."+s);

		String s1 = topicSpliter(s, "0");
		log.info(".........."+s1);
		String x= StrUtil.split(s1, "_").get(0);
		log.info(x);

		System.out.println("a".equals(null));

		try {

			m();
		} catch (Exception e) {

			log.error(e.getMessage());
		}


		log.info("after error2");

new Thread(new Xthread()).start();
	}

	public static void m() {
		Preconditions.checkArgument(1>2,"erro here .....");
		log.info("after error");
	}
	public static void main2(String args[]) {

		Integer integer = Integer.valueOf("2");
		System.out.println(integer);


		String ip = "12.3.06.10";
		String s1 = ip.replaceAll("\\.", "_");
		System.out.println(s1);

		String strSrc = "${1} ${2} and ${23[3]} and ${2[4]}  and ${23[channel]} and ${3[channel]}";


		// 正则:  ${number}
		String rex_slot_number = "\\$\\{(\\d+)}";
		// 正则: ${number[number]}
		String rex_s_ip = "\\$\\{(\\d+)\\[(\\d)]}";
		// 正则: ${number[channel]}
		String rex_s_chanel = "\\$\\{(\\d+)\\[(channel)]}";
		ArrayList<String> rexs = new ArrayList<>();
		rexs.add(rex_slot_number);
		rexs.add(rex_s_ip);
		rexs.add(rex_s_chanel);

		for (String rex : rexs) {
			Pattern compile = Pattern.compile(rex);
			Matcher matcher = compile.matcher(strSrc);
			int i = matcher.groupCount();

			if (i == 1) {
				while (matcher.find()) {
					String group = matcher.group();
					String group1 = matcher.group(1);
					System.out.println(group);
					System.out.println(strSrc);
					String s = strSrc.replaceAll(group, "123.3.4.5");
					String s2 = matcher.replaceAll( "123.3.4.5");
					//String s3 = matcher.appendReplacement(group, "123.3.4.5");
					System.out.println(s);
					System.out.println(s2);
				}
			} else if (i == 2) {
				while (matcher.find()) {
					String group = matcher.group();
					String group1 = matcher.group(1);
					String group2 = matcher.group(2);

				}
			}


		}
	}

	public static boolean validPrefix(String topicPrefix, String topic) {
		boolean flag = false;
		if (topic.startsWith(topicPrefix)) {
			flag = true;
		} else {
			Log.debug("mather fail");
		}
		return flag;
	}

	public static String topicSpliter(String topic, String index) {

		boolean numeric = isNumeric(index);
		boolean end = ObjectUtil.equals(index, "end");
		String format = StrUtil.format("args index[{}] is not valid.", index);
		Preconditions.checkArgument(numeric || end,format);


		String result=topic;
		String[] split = topic.split("/");

		for (String str : split) {
			log.debug(str);
		}

		if (end) {
			result = split[split.length - 1];
		} else if (numeric) {
			result = split[Integer.valueOf(index)];
		}

		return result;

	}


	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}
}
