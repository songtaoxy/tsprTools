package com.st.api.practice.regx;

/**
 * @author: st
 * @date: 2021/5/29 16:05
 * @version: 1.0
 * @description:
 */
public class ReplaceStr {




	public static void main(String[] args) {
		String paramPattern = "param\\$\\(\\w+\\)"; // 匹配"param$(id)"

		String s1 = "param$(p1)";
		String s2 = "xxx param$(p1)";

		String replaceMent0 = "ac";
		String replaceMent = "\"" + replaceMent0 + "\"";

		//String s3 = s2.replaceAll(paramPattern, "ac");
		String s3 = s2.replaceAll(paramPattern, replaceMent);
		System.out.println(s3);
		//String s2 =
	}
}
