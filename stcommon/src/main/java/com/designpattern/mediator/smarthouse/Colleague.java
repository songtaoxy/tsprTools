package com.designpattern.mediator.smarthouse;

import org.codehaus.plexus.interpolation.util.ValueSourceUtils;
import org.sonatype.aether.spi.log.NullLogger;


/**
 * ͬ���� <br>
 *<br>
 * ��γ�ȡ? Alarm, TV, ... �����໥Э����"ͬ��" <br>
 * ��Ȼ, Ҳ���Ը���ʵ�ʽ��г�ȡ, ���� "Device �豸" <br>
 */
public abstract class Colleague {
	private Mediator mediator;
	public String name;

	public Colleague(Mediator mediator, String name) throws NoSuchMethodException {

		this.mediator = mediator;
		this.name = name;

		System.out.println("\n=========start===========");
		System.out.println(this+"..........");
		System.out.println(this.getClass().getMethod("GetMediator", null));
		System.out.println(super.getClass().getName());
		System.out.println("=========== end ===========");


	}

	public Mediator GetMediator() {
		return this.mediator;
	}

	public abstract void SendMessage(int stateChange);
}
