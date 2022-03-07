package com.st.practice.exception;

/**
 * @author: st
 * @date: 2022/3/7 01:11
 * @version: 1.0
 * @description:
 */
public class MyBusinessException extends Exception {
  private static final long serialVersionUID = 3811540098579060059L;

  public MyBusinessException(String s, Throwable e) {
    super(s, e);
  }
}

class ExceptionWrapper {

  public static void main(String[] args) throws MyBusinessException {
    wrapException("100x"); //
  }

  public static void wrapException(String input) throws MyBusinessException {
    try {
      // do something
      int i = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      throw new MyBusinessException("A message that describes the error.", e);
    }
  }
}
