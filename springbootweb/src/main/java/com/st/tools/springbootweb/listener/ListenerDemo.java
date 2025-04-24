package com.st.tools.springbootweb.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationListener;


/**
 * @author: st
 * @date: 2021/11/12 16:37
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ListenerDemo implements ApplicationListener<AvailabilityChangeEvent> {

	@Override
	public void onApplicationEvent(AvailabilityChangeEvent event) {
    System.out.println ("监听器: 监听到事件：" + event);
		if (ReadinessState.ACCEPTING_TRAFFIC == event.getState()){
			System.out.println("监听器: 应用启动完成，可以请求了……");
		}

		AvailabilityState state = event.getState();
		System.out.println("监听器: 监听到的:"+state.toString());
	}
}
