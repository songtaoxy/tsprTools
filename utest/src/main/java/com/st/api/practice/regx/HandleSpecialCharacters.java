package com.st.api.practice.regx;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: st
 * @date: 2021/5/28 17:26
 * @version: 1.0
 * @description:
 */
@Slf4j
public class HandleSpecialCharacters {

	public static void main(String[] args) {

		String targetStr = "select id, tenantid, pubts, effectivedate , countryzone, businessid \nfrom org.func.ITOrg  \nwhere id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) \ngroup by tenantid \nlimit 100 \n";


		String resultStr= handleSpecialCharacters(targetStr);
	}

	public static String replaceLine() {

		String str = "select id, tenantid, pubts, effectivedate , countryzone, businessid \nfrom org.func.ITOrg  \nwhere id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) \ngroup by tenantid \nlimit 100 \n";
		if (null != str && !"".equals(str)) {
			//Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
			Pattern pattern = Pattern.compile("\\s+\t|\r|\n");
			Matcher matcher = pattern.matcher(str);

			String s = matcher.replaceAll(" ");
			log.info(s);
			return s;
		}
		return null;
	}

	/**
	 * 功能: 处理yonql脚本中的特殊字符, 如换行符, 多个空格等. 相关case, see 测试案例
	 *
	 * <p></p>
	 * 前端输入:<br>
	 * <pre>
	 * select id, tenantid, pubts, effectivedate , countryzone, businessid
	 * from org.func.ITOrg
	 * where id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5)
	 * group by tenantid
	 * limit 100
	 * </pre>
	 *
	 *<p></p>
	 * 后端接收:<br>
	 * <pre>"select id, tenantid, pubts, effectivedate , countryzone, businessid \nfrom org.func.ITOrg  \nwhere id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) \ngroup by tenantid \nlimit 100 \n"
	 * </pre>
	 *
	 * <p></p>
	 * 期望格式:<br>
	 * <pre>select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg where id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) group by tenantid limit 100
	 * </pre>
	 * @param targetStr
	 * @return
	 */
	public static String handleSpecialCharacters(String targetStr) {

		log.info("string before handled:[{}]", targetStr);

		if (null != targetStr && !"".equals(targetStr)) {
			Pattern pattern = Pattern.compile("\\s+\t|\r|\n");
			Matcher matcher = pattern.matcher(targetStr);
			String middleStr = matcher.replaceAll(" ").trim();

			pattern = Pattern.compile("\\s+");
			matcher = pattern.matcher(middleStr);
			String resultStr = matcher.replaceAll(" ").trim();

			log.info("string after handled:[{}]", resultStr);

			return resultStr;
		}
		return null;
	}

}
