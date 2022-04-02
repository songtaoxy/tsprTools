package com.st.practice.proxy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: st
 * @date: 2022/4/2 10:33
 * @version: 1.0
 * @description:
 */
// @Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface Test2 {

  String value() default "test2 annotation default value";
}

@Test2("注解的本质")
public class AnnoTest {
  public static void main(String[] args) {

    // 默认, 生成文件, 如代理类不会不保存. 通过环境变量或main方法中的设置, 则可以
    // 新本jdk
    // System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
    System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

    Test2 declaredAnnotation = AnnoTest.class.getDeclaredAnnotation(Test2.class);

    System.out.println(declaredAnnotation.value());
  }
}
