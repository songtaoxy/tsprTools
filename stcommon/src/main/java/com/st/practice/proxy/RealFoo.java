package com.st.practice.proxy;

import java.lang.reflect.*;
import java.util.Arrays;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author: st
 * @date: 2022/1/11 12:46
 * @version: 1.0
 * @description:
 */
public class RealFoo implements Foo {

  @Override
  public String ping(String name) {
    System.out.println("ping");
    return "pong";
  }
}

interface Foo {

  String ping(String name);
}

class MyInvocationHandler implements InvocationHandler {

  // 目标对象
  private final Object target;

  public MyInvocationHandler(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("proxy - " + proxy.getClass());
    System.out.println("method - " + method);
    System.out.println("args - " + Arrays.toString(args));
    return method.invoke(target, args);
  }
}

class client{
  public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Foo foo = new RealFoo();
    // 根据类加载器和接口数组获取代理类的Class对象
    Class<?> proxyClass = Proxy.getProxyClass(Foo.class.getClassLoader(), Foo.class);

    // 通过Class对象的构造器创建一个实例（代理类的实例）
    Foo fooProxy = (Foo) proxyClass.getConstructor(InvocationHandler.class)
            .newInstance(new MyInvocationHandler(foo));

    // 调用 ping 方法，并输出返回值
    String value = fooProxy.ping("杨过");
    System.out.println(value);

  }


}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//




final class $Proxy0 extends Proxy implements Foo {
  private static Method m1;
  private static Method m2;
  private static Method m3;
  private static Method m0;

  public $Proxy0(InvocationHandler var1) throws  Exception  {
    super(var1);
  }

  public final boolean equals(Object var1) throws UndeclaredThrowableException  {
    try {
      return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
    } catch (RuntimeException | Error var3) {
      throw var3;
    } catch (Throwable var4) {
      throw new UndeclaredThrowableException(var4);
    }
  }

  public final String toString() throws UndeclaredThrowableException  {
    try {
      return (String)super.h.invoke(this, m2, (Object[])null);
    } catch (RuntimeException | Error var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final String ping(String var1) throws UndeclaredThrowableException  {
    try {
      return (String)super.h.invoke(this, m3, new Object[]{var1});
    } catch (RuntimeException | Error var3) {
      throw var3;
    } catch (Throwable var4) {
      throw new UndeclaredThrowableException(var4);
    }
  }

  public final int hashCode() throws  UndeclaredThrowableException  {
    try {
      return (Integer)super.h.invoke(this, m0, (Object[])null);
    } catch (RuntimeException | Error var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
      m2 = Class.forName("java.lang.Object").getMethod("toString");
      m3 = Class.forName("com.st.practice.proxy.Foo").getMethod("ping", Class.forName("java.lang.String"));
      m0 = Class.forName("java.lang.Object").getMethod("hashCode");
    } catch (NoSuchMethodException var2) {
      throw new NoSuchMethodError(var2.getMessage());
    } catch (ClassNotFoundException var3) {
      throw new NoClassDefFoundError(var3.getMessage());
    }
  }
}

