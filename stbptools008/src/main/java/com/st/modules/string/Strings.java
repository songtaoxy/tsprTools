package com.st.modules.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <pre>
 * 目的: 随机生成10个长度在[1-10]以内的随机字符串, 用于快速测试.
 *
 * 要求:
 * 1.创建完List<String>之后，往其中添加十条随机字符串
 * 2.每条字符串的长度为10以内的随机整数
 * 3.每条字符串的每个字符都为随机生成的字符，字符可以重叠
 * 4.每条随机字符串不可重复
 *
 * sort:
 * 从左向右原则，且0-9
 * 数字优先原则，且A-Zk
 * 字母大写优先原则，且a-z
 *
 * Ref: <a href="https://www.jianshu.com/p/61db371f1635">related doc</a>
 * </pre>
 *
 * @author: st
 * @date: 2022/3/11 22:10
 * @version: 1.0
 * @description:
 */
public class Strings {
  public static void main(String[] args) {

    List<String> strList;
    strList = randomStrings();
    //strList = randStrsWithLen(10, 10);

    System.out.println("------随机生成的10条字符串-------");
    for (String string : strList) {
      System.out.println(string);
    }
    System.out.println("------------排序后------------");
    Collections.sort(strList);
    for (String string : strList) {
      System.out.println(string);
    }
  }

  /**
   * default: 10个长度在10以内的随机字符串
   *
   * <p>
   *
   * @return
   */
  public static List<String> randomStrings() {
    // 将所有的大小写字母和0-9数字存入字符串中
    String str = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
    Random random = new Random();
    List<String> listString = new ArrayList<String>();
    String strArray[] = new String[10];
    // 生成10条长度为1-10的随机字符串
    for (int i = 0; i < 10; i++) {
      StringBuffer stringBuffer = new StringBuffer();
      // 确定字符串长度
      int stringLength = (int) (Math.random() * 10);
      for (int j = 1; j <= stringLength; j++) {
        // 先随机生成初始定义的字符串 str 的某个索引，以获取相应的字符
        int index = random.nextInt(str.length());
        char c = str.charAt(index);
        stringBuffer.append(c);
      }
      // 判断当前的list容器中是否已有刚生成的字符串，满足每条字符串不可重复性
      if (!(listString.contains(stringBuffer.toString()))) {
        listString.add(stringBuffer.toString());
      } else {
        i--;
      }
    }
    return listString;
  }

  /**
   * 生成 <Count> 个长度在<Lenth>以内的随机字符串
   *
   * <p>
   *
   * @param Count
   * @param Length
   * @return
   */
  public static List<String> randStrsWithLen(int Count, int Length) {
    // 将所有的大小写字母和0-9数字存入字符串中
    String str = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
    Random random = new Random();
    List<String> listString = new ArrayList<String>();
    String strArray[] = new String[10];
    // 生成10条长度为1-10的随机字符串
    for (int i = 0; i < Count; i++) {
      StringBuffer stringBuffer = new StringBuffer();
      // 确定字符串长度
      int stringLength = (int) (Math.random() * Length);
      for (int j = 0; j < stringLength; j++) {
        // 先随机生成初始定义的字符串 str 的某个索引，以获取相应的字符
        int index = random.nextInt(str.length());
        char c = str.charAt(index);
        stringBuffer.append(c);
      }
      // 判断当前的list容器中是否已有刚生成的字符串，满足每条字符串不可重复性
      if (!(listString.contains(stringBuffer.toString()))) {
        listString.add(stringBuffer.toString());
      } else {
        i--;
      }
    }
    return listString;
  }
}
