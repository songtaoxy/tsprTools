package com.st.modules.string;


import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2021/11/11 15:46
 * @version: 1.0
 * @description:
 */
@Slf4j
public class StringUtils {

  public static final String LINE = "\n";
  public static final String BLANK_STRING = " ";
  public static final String SPLIT_LINE = "=======================";
  public static final String START = "Start";
  public static final String START_FUll =
      SPLIT_LINE + BLANK_STRING + START + BLANK_STRING + SPLIT_LINE + LINE;
  public static final String END = "End";
  public static final String END_FULLE =
      LINE
          + SPLIT_LINE
          + BLANK_STRING
          + BLANK_STRING
          + END
          + BLANK_STRING
          + BLANK_STRING
          + SPLIT_LINE;

  /**
   * convert "Object[]" to "String"; 同java15的文本快功能.
   *
   * <ul>
   * <li>将字符串拆分成(对象/字符串)数组, 然后将数组重新拼接成字符串</li>
   * <li>目的: 方便,且能避免错误. 根据各个要素(字符串, 变量, 转移字符,...),就能得到字符串</li>
   * <li>补充: Java字符串拼接太麻烦, 没有python, scala等语言的三引号</li>
   * <li>场景:log输出时,构建需要的字符串, ...</li>
   * </ul>
   *
   * @param strArray 字符串的各个要素
   * @return 由数组拼接的字符串
   */
  public static String objs2Str(Object[] strArray) {

    List<Object> logObj = Arrays.asList(strArray);
    String resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining());
    // resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining(","));
    // resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining("-"));
    // resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining("_"));

    //LogUtils.formatObjAndLogging(resultStr, "");

    return resultStr;
  }

  /**
   * 判断字符串是否是数字 {@code java.lang.Number}
   * @param str
   * @return
   */
  public static  boolean isNumeric(String str){
    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher isNum = pattern.matcher(str);
    if( !isNum.matches() ){
      return false;
    }
    return true;
  }
}
