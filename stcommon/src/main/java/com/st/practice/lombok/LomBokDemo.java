package com.st.practice.lombok;

import com.st.practice.logback.LogbackDemo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.applet.AppletClassLoader;
import sun.misc.Launcher;

/**
 * @author: st
 * @date: 2022/12/2 01:33
 * @version: 1.0
 * @description:
 */
@Slf4j
@NoArgsConstructor
public class LomBokDemo {

	public static void main(String[] args) {

		LogbackDemo logbackDemo = new LogbackDemo();

		log.info(logbackDemo.toString());
		log.info(String.valueOf(logbackDemo.hashCode()));


		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		ClassLoader classLoader = Launcher.getLauncher().getClassLoader();

		String x = "s";

	}
}
