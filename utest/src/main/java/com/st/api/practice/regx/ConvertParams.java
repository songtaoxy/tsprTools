package com.st.api.practice.regx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: st
 * @date: 2021/5/29 14:44
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ConvertParams {
	public static void main(String[] args) {
		//t1();

		String strWithFormalParams = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg where id>param$(p1) and tenantid<param$(p2) and tenantid between param$(p3) and param$(p4) and businessid=param$(p5) group by tenantid limit 100";

		Map<String, Object> param = getParam();

		String strWihtActualParams = formalParams2rActualParams(strWithFormalParams, param);

		log.info("strWithFormalParamss ===> strWihtActualParams:\n [strWithFormalParams]===>{}\n [strWihtActualParams]===>{}\n", strWithFormalParams,strWihtActualParams);
	}

	private static Map<String, Object> getParam() {
		Map<String, Object> param = new LinkedHashMap<String, Object>();
		Map<String, String> modelParam = new LinkedHashMap<String, String>();
		param.put("modelParam", modelParam);
		modelParam.put("p1", "actualP1");
		modelParam.put("p2", "actualP2");
		modelParam.put("p3", "actualP3");
		modelParam.put("p4", "actualP4");
		modelParam.put("p5", "actualP5");
		log.info(String.valueOf(param));
		return param;
	}


	@Test
	 void t1() {
		String resultStr = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg where id>param$(p1) and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) group by tenantid limit 100";

		String paramPattern = "param\\$\\(\\w+\\)";
		String sentencePattern = ".*param\\$\\(\\w+\\).*";
		boolean isMathch = Pattern.matches(sentencePattern, resultStr);


		Matcher matcherFull = buildRegexMatcher(resultStr, paramPattern);
		//Matcher matcherFull = buildRegexMatcher(resultStr, "(?<=param\\$\\()\\w+(?=\\))");
		//Matcher matcherFull = buildRegexMatcher(resultStr, "200");
		//boolean b = matcherFull.find(0);
		//log.info(String.valueOf(b));


		while (matcherFull.find()) {
			String full = matcherFull.group();
			System.out.println(full);
		}

	}

	private static Matcher buildRegexMatcher(String targetStr, String pattern) {
		Pattern patternFull = Pattern.compile(pattern);
		return patternFull.matcher(targetStr);
	}


	public static String formalParams2rActualParams(String targetStr, Map param) {
		String strWithFormalParams = targetStr;

		Map modelParam = (Map) param.get("modelParam");
		String strWihtActualParams = null;

		String sentencePattern = ".*param\\$\\(\\w+\\).*";
		String paramPattern = "param\\$\\(\\w+\\)"; // 匹配"param$(id)"
		String filedPattern = "(?<=param\\$\\()\\w+(?=\\))"; // 匹配"param$(id)"中的"id"


		boolean isMathch = Pattern.matches(sentencePattern, strWithFormalParams);
		if (!isMathch) {
			return strWithFormalParams;
		}


		Matcher matcherFull = buildRegexMatcher(strWithFormalParams, paramPattern);
		while (matcherFull.find()) {
			String formalParam = matcherFull.group();

			Matcher matcherFiled = buildRegexMatcher(formalParam, filedPattern);
			while (matcherFiled.find()) {
				String field = matcherFiled.group();
				String paramSpecialPattern = "param\\$\\(" + field + "\\)";

				String filedValue = (String) modelParam.get(field);
				log.info("[formalParam : field : filedValue] ===> [{} : {} : {}]", formalParam, field, filedValue);

				strWithFormalParams = strWithFormalParams.replaceAll(paramSpecialPattern, filedValue);
			}
		}
		return strWithFormalParams;
	}

}
