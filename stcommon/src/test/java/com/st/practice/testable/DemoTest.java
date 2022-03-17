package com.st.practice.testable;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.google.errorprone.annotations.Var;
import org.junit.jupiter.api.Test;

import static com.alibaba.testable.core.matcher.InvocationVerifier.verifyInvoked;
import static org.junit.jupiter.api.Assertions.*;

public class DemoTest {

  Demo demo = new Demo();



  public static class Mock {

    @MockInvoke
    private String substring(String self, int i, int j) {
    	return "sub";
    }

    @MockInvoke(targetClass = Demo.class)
    private String sub(String str, int s, int e) {
      return "sub2";
    }
  }
  @Test
  void sub() {
    String sub = demo.sub("sub", 1, 2);
    System.out.println(sub);
  }
  @Test
  void sub2() {

    String sub = demo.sub2("sub", 1, 2);

    // assertEquals("sub",sub,"hi");
    System.out.println(sub);

    verifyInvoked("sub").with("sub", 1, 2);
  }
}
