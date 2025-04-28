package com.st.tools.common.exception;

public class ExceptionUtils {

    public static String getCause(Throwable ex) {
        Throwable current = ex;
        String msg = null;
        String name = null;

        if(null != ex.getMessage()) {return ex.getMessage();}

        while (current != null) {
            System.out.println("异常类型: " + current.getClass().getName());
            System.out.println("异常信息: " + current.getMessage());
            msg = current.getMessage();
            name = current.getClass().getName();
            current = current.getCause();
        }
      return null == msg?name:msg;
    }}
