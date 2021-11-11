package com.st.utils.string;

import com.st.utils.log.UtilsLogs;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: st
 * @date: 2021/11/11 15:46
 * @version: 1.0
 * @description:
 */
@Slf4j
public class UtilsString {

  public static final String LINE = "\n";
  public static final String BLANK_STRING = " ";
  public static final String SPLIT_LINELINE = "=======================";
  public static final String START = "Start";
  public static final String START_FUll =
      SPLIT_LINELINE + BLANK_STRING + START + BLANK_STRING + SPLIT_LINELINE + LINE;
  public static final String END = "End";
  public static final String END_FULLE =
      LINE
          + SPLIT_LINELINE
          + BLANK_STRING
          + BLANK_STRING
          + END
          + BLANK_STRING
          + BLANK_STRING
          + SPLIT_LINELINE;

  public static String objs2Str(Object[] strArray) {

    List<Object> logObj = Arrays.asList(strArray);
    String resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining());
    // resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining(","));
    // resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining("-"));
    // resultStr = logObj.stream().map(Objects::toString).collect(Collectors.joining("_"));
    UtilsLogs.formatObjAndLogging(resultStr, "");

    return resultStr;
  }
}
