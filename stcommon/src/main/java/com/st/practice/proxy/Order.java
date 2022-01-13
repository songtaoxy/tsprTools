package com.st.practice.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author: st
 * @date: 2022/1/11 12:42
 * @version: 1.0
 * @description:
 */
public class Order {

	static class OrderServiceImpl{

		public void payForGoods(String goods) {
			System.out.println("下单 " + goods + " 商品成功！");
		}
	}

	public static void main(String[] args) {
		OrderServiceImpl orderService = new OrderServiceImpl();
		OrderServiceImpl orderProxy = (OrderServiceImpl) Enhancer.create(orderService.getClass(), new MethodInterceptor() {
			@Override
			public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
				System.out.println("当前时间：" + LocalDateTime.now());
				Object result = methodProxy.invokeSuper(o, objects);
				return result;
			}
		});
		orderProxy.payForGoods("牛奶");
	}
}
