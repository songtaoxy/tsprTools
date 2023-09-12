package com.st.utils.string;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.st.utils.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * S: symbol
 *
 * <p>单个字符, 在引用时, 占据的空间小. <br>
 * 如果类名, 已经变量名太长, 则拼接时不易读
 *
 * @author: st
 * @date: 2021/11/17 02:31
 * @version: 1.0
 * @description:
 */

@Slf4j
public class S {

  /** l: Line */
  public static final String L = "\n";

  /** b: blank */
  public static final String B = " ";

  public static final String FS = File.separator;

  /** s: split line */
  public static final String S =  "\n=======================================================\n";

  public static final String SS = "\n======================== start ========================\n";
  public static final String SE = "\n========================  end  ========================\n";

  public static void main(String[] args) {

    System.out.println("hi");
    //LogUtils.foal(FS,"");
  }
}
