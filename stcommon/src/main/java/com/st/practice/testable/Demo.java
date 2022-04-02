package com.st.practice.testable;

import java.util.HashMap;

/**
 * @author: st
 * @date: 2022/3/17 16:21
 * @version: 1.0
 * @description:
 */
public class Demo {

  public String sub(String str,int s, int e) {

    return str.substring(s,e);


  }

  public String sub2(String str,int s, int e){
    return this.sub(str, s, e);
  }
}

