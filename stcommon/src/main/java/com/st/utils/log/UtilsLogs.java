package com.st.utils.log;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * @author: st
 * @date: 2021/11/6 15:09
 * @version: 1.0
 * @description:
 */
@Slf4j
public class UtilsLogs {

  /**
   * @deprecated since 2021.11.11 by ts replaced by "local vars"
   */
  String ps;

  /**
   * 打印日志, 格式化, 方便查看<p>
   *
   * 封装和补充日志打印:  {@code
   *                          String str = "select * from t_table"
   *                          login.info{"the external message is: "+ var}} <p>
   *
   * details see: {@link  UtilsLogs#formatObjAndLogging_old(Object, String)}<p>
   *
   * @param obj 日志要将该对象的内容打印出来
   * @param infoTips 对"obi"的补充说明
   */
  public static void formatObjAndLogging(Object obj, String infoTips) {

    /* 当补充信息没有传, 即为null时, 给默认值 */
    String value_null = "There external messages is null.";

    /* 当补充信息为空字符串时, 给默认值 */
    String value_blank = "There external messages is blank.";

    infoTips =
        Optional.ofNullable(infoTips)
            .map(str -> str.length() == 0 ? value_blank : str)
            .orElse(value_null);

    log.info(
        "\n\n"
            + "================================== start =====================================\n"
            + "- [Type    ]:" + obj.getClass().getName() + "\n"
            + "- [messsage]:" + infoTips                 + "\n"
            + "- [content ]:\n"
            + "{}\n"
            + "===================================  end  ====================================\n\n",
        JSON.toJSONString(obj, true));
  }


  /**
   * @deprecated since 2021.11.11 by ts, and replaced by {@link UtilsLogs#formatObjAndLogging(Object, String)}
   * @param obj 日志要将该对象的内容打印出来
   * @param infoTips 对"obi"的补充说明
   */
  public static void formatObjAndLogging_old(Object obj, String infoTips){
    // 没有传入说明信息时, 该如何处理?
    // 传入为null
    // 传入为空: 这里null, 和空, 采用相同的处理方式. 即,给默认值.
    // 传入正常值
    String value_null = "There external messages is null.";
    String value_blank = "There external messages is blank.";

    // 方式1: if else 直接粗暴
    /* if (StringUtils.isEmpty(infoTips)) {
      infoTips = "there is no prompt messages";
    }*/

    // option 这种简单的方式, 并不能突显option的优雅; 相对于if else
    /* Optional<String> o =
    Optional.ofNullable(infoTips)
        .map(
            (str) -> {
              if (StringUtils.isEmpty(str)) {
                str = "no";
              }
              return Optional.ofNullable(str);
            })
        .orElse(Optional.of("no"))*/ ;

    // option 这种简单的方式, 并不能突显option的优雅; 相对于if else
    /* String o1 =
    Optional.ofNullable(infoTips)
        .map(
            (str) -> {
              if (StringUtils.isEmpty(str)) {
                str = "no";
              }
              // return Optional.ofNullable(str);
              return str;
            })
        .orElse("no")*/ ;

    // option 这种简单的方式, 并不能突显option的优雅; 相对于if else
    infoTips =
            Optional.ofNullable(infoTips)
                    .map(str -> str.length() == 0 ? value_blank : str) // 为空时, 该如何处理
                    .orElse(value_null); // 为null时, 该如何处理

    /* infoTips = o.get();
    infoTips = o1*/ ;
    // infoTips = o2;

    log.info(
            "\n\n"
                    + "================================== start =====================================\n"
                    + "- [Type    ]:" + obj.getClass().getName() + "\n"
                    + "- [messsage]:" + infoTips                 + "\n"
                    + "- [content ]:\n"
                    + "{}\n"
                    + "===================================  end  ====================================\n\n",
            JSON.toJSONString(obj, true));
  }

}
