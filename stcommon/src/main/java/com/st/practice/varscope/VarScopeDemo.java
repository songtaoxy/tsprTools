package com.st.practice.varscope;

import java.io.ObjectOutputStream;

/**
 * @author: st
 * @date: 2022/3/20 02:09
 * @version: 1.0
 * @description:
 */
public class VarScopeDemo {

  public static void main(String[] args) {

    PP pp = new PP("pp");
    PP pp1 = new PP("pp1");

    PP x;

    x = pp;
    pp = pp1;

    System.out.println(x.getName());
  }
}


class PP {
  public String getName() {
    return name;
  }

  private String name;

  public PP(String name) {
    super();
    this.name = name;
  }
}
