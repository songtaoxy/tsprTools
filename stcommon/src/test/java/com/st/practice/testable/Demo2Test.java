package com.st.practice.testable;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.google.errorprone.annotations.Var;
import org.junit.jupiter.api.Test;

class Demo2Test {

  Demo2 demo2 = new Demo2();

  public static class Mock {
    @MockInvoke(targetClass = Demo2.class, targetMethod = "sub2")
    public String sub2(String str, int s, int e) {
      return null;
    }

    @MockInvoke(targetClass = Demo2.class, targetMethod = "sub")
    public String sub(String str, int s, int e) {
      return "hi";
    }
  }

  @Test
  void sub() {}

  @Test
  void sub2() {
    String hiiii = demo2.sub2("hiiii", 1, 2);
    System.out.println(hiiii);
  }

}
