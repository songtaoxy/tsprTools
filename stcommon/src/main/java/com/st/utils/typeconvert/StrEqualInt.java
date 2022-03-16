package com.st.utils.typeconvert;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 判断字符串是否是数字
 * 
 * </pre>
 * @author: st
 * @date: 2022/3/16 16:49
 * @version: 1.0
 * @description:
 */
public class StrEqualInt {
  public static void main(String[] args) {

    String s = "-1";
    System.out.println(StringUtils.isNumeric(s));
    System.out.println(Integer.parseInt(s));


    System.out.println(isInteger("-19162431.1254"));
    System.out.println(isInteger("-1"));
    System.out.println(isInteger("1"));
    System.out.println(isInteger("1.2"));
    System.out.println(isInteger("x1.2"));


    System.out.println(isNumbericwithExcetpion("-19162431.1254"));
    System.out.println(isNumbericwithExcetpion("-1"));
    System.out.println(isNumbericwithExcetpion("1"));
    System.out.println(isNumbericwithExcetpion("1.2"));
    System.out.println(isNumbericwithExcetpion("x1.2"));
  }

  /**
   * <pre>
   *
   * 所有数字: 正负, 小数, 整数
   *
   * System.out.println(isInteger("-19162431.1254"));
   * System.out.println(isInteger("-1"));
   * System.out.println(isInteger("1"));
   * System.out.println(isInteger("1.2"));
   * System.out.println(isInteger("x1.2"));
   *
   * true
   * true
   * true
   * true
   * false
   *
   * </pre>
   *
   * @param str
   * @return
   */
  public static boolean isNumberic(String str) {

    // 该正则表达式可以匹配所有的数字 包括负数
    Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    String bigStr;
    try {
      bigStr = new BigDecimal(str).toString();
    } catch (Exception e) {
      return false; // 异常 说明包含非数字。
    }

    Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
    if (!isNum.matches()) {
      return false;
    }
    return true;
  }

  /**
   * 所有数字: 正负, 小数, 整数
   *
   * <pre>
   * System.out.println(isNumbericwithExcetpion("-19162431.1254"));
   * ystem.out.println(isNumbericwithExcetpion("-1"));
   * System.out.println(isNumbericwithExcetpion("1"));
   * System.out.println(isNumbericwithExcetpion("1.2"));
   * System.out.println(isNumbericwithExcetpion("x1.2"));
   * rue
   * true
   * true
   * true
   * false
   *
   *
   * </pre>
   *
   * @param str
   * @return
   */
  public static boolean isNumbericwithExcetpion(String str) {
    String bigStr;
    try {
      bigStr = new BigDecimal(str).toString();
    } catch (Exception e) {
      return false; // 异常 说明包含非数字。
    }
    return true;
  }

  /**
   *
   * 适用范围:  正负整数
   *
   * <pre>
   * System.out.println(isInteger("-19162431.1254"));
   * System.out.println(isInteger("-1"));
   * System.out.println(isInteger("1"));
   * System.out.println(isInteger("1.2"));
   * System.out.println(isInteger("x1.2"));
   *
   * false
   * true
   * true
   * false
   * false
   * </pre>
   *
   * @param input
   * @return
   */
  public static boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
