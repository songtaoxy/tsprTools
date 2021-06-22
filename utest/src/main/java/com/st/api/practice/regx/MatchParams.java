package com.st.api.practice.regx;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: st
 * @date: 2021/5/24 10:55
 * @version: 1.0
 * @description:
 */
public class MatchParams {
    public static void main(String[] args) {
        Map<String,Object> single = new HashMap<>();
        single.put("paramName","me");
        single.put("mean","wonders");

//        List<String> one = Lists.newArrayList("paramName","mean");
//        List<String> one = new ArrayList("paramName","mean");
        List<String> one = new ArrayList();
        one.add("paramName");
        one.add("mean");

        String content = "select * from account WHERE field_name1 = $param.paramName and field_name2 = $global.data and field_name3=       $mean and field_name4 = $dream";

        Iterator<String> iterator = one.iterator();
        while (iterator.hasNext()) {
            String e = iterator.next();
            String value = (String) single.get(e);
            // 先匹配现有的内容
            content = matchReplaceWithCondition(content, e, value);
        }

        /*for (String e : ListUtils.emptyIfNull(one)) {
//            String value = MapUtils.getString(single, e);
            String value = (String) single.get(e);
            // 先匹配现有的内容
            content = matchReplaceWithCondition(content, e, value);
        }*/
        // 匹配参数内容为空的情况
        content = getReplaceSql(content).trim();
        if(StringUtils.endsWithIgnoreCase(content,"where")){
            content = StringUtils.removeEndIgnoreCase(content,"where");
        }
        System.out.println("last "+content);
    }

    public static String matchReplaceWithCondition( String  content,String condition,String value)
    {
        String pattern = "\\$([a-zA-Z0-9_.]*)" + condition;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            m.appendReplacement(sb, group == null ? "" : ("\"").concat(value).concat("\""));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String getReplaceSql(String content) {
        String words = "([a-zA-Z0-9._]*)";
        String  patternReg = (words+"\\s*=\\s*\\$"+words);
        // 处理遗留的逗号
        String pattern = patternReg+" and|"+patternReg+"|and "+patternReg;

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String value = m.group();
            m.appendReplacement(sb, value == null ? "" : "");
        }
        m.appendTail(sb);
        System.out.println("getReplaceSql: "+sb.toString());
        return sb.toString();
    }
}
