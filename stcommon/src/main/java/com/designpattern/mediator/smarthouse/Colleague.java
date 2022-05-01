package com.designpattern.mediator.smarthouse;

import org.codehaus.plexus.interpolation.util.ValueSourceUtils;
import org.sonatype.aether.spi.log.NullLogger;


/**
 * 同事类 <br>
 *<br>
 * 如何抽取? Alarm, TV, ... 都是相互协作的"同事" <br>
 * 当然, 也可以根据实际进行抽取, 比如 "Device 设备" <br>
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
