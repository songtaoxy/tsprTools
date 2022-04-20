package com.designpattern.mediator.smarthouse;

import org.codehaus.plexus.interpolation.util.ValueSourceUtils;
import org.sonatype.aether.spi.log.NullLogger;

//同事抽象类
public abstract class Colleague {
	private Mediator mediator;
	public String name;

	public Colleague(Mediator mediator, String name) throws NoSuchMethodException {

		this.mediator = mediator;
		this.name = name;

		System.out.println(this+"..........");
		System.out.println(this.getClass().getMethod("GetMediator", null));
		System.out.println(super.getClass().getName());


	}

	public Mediator GetMediator() {
		return this.mediator;
	}

	public abstract void SendMessage(int stateChange);
}
