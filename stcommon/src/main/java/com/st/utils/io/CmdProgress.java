package com.st.utils.io;

/**
 * @author: st
 * @date: 2021/12/29 03:12
 * @version: 1.0
 * @description:
 */
public class CmdProgress {
	private static byte anim = 0;

	public static void animate() {
		switch (anim) {
			case 1:
				System.out.print("\r \\");
				break;
			case 2:
				System.out.print("\r |");
				break;
			case 3:
				System.out.print("\r /");
				break;
			default:
				anim = 0;
				System.out.print("\r -");
		}
		anim++;
	}

	public static void clearLine() {
		System.out.print("\r    \r");
	}
}
