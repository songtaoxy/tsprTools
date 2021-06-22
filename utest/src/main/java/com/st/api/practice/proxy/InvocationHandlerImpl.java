package com.st.api.practice.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author: st
 * @date: 2021/4/23 00:59
 * @version: 1.0
 * @description:
 */
public class InvocationHandlerImpl implements InvocationHandler {
    Object targetObject;

    public InvocationHandlerImpl(Object targetObject) {
        this.targetObject = targetObject;
    }

    // proxy代表代理对象本身（此处没有使用）
    // method代表被代理接口中当前被调用的method对象
    // args为method对象参数
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // your code before method invoked
        System.out.println("start to do something..");
        Object result = method.invoke(targetObject, args);
        // your code after method invoked
        System.out.println("end to do something..");
        return result;
    }

    public static void main(String[] args) {
        // 将内存中生成的代理类保存到本地.代理类是直接在内存中生成, 默认不保存
        Properties properties = System.getProperties();
        properties.put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");


        // JDK代理只能代理实现了接口的对象，
        // 创建这个对象传入invocationHandler实现类中
        // 后者调用invoke方法时，method的反射调用会使用到该对象
        Person person = new Person() {
            @Override
            public void eat() {
                System.out.println("eating...");
            }

            @Override
            public void drink() {
                System.out.println("drinking...");
            }
        };
        Person proxyPerson = (Person) Proxy.newProxyInstance(Person.class.getClassLoader(), new Class[]{Person.class}, new InvocationHandlerImpl(person));
        proxyPerson.eat();
    }
}