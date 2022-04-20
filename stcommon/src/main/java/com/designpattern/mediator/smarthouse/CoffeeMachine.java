package com.designpattern.mediator.smarthouse;

import com.st.utils.string.StringUtils;

public class CoffeeMachine extends Colleague {

	public CoffeeMachine(Mediator mediator, String name) throws NoSuchMethodException {
		super(mediator, name);
		// TODO Auto-generated constructor stub
		mediator.Register(name, this);

		System.out.println(this+"========");
		System.out.println(super.hashCode()+"*******");
		System.out.println(this.getClass().getMethod("GetMediator",null));
	}

	@Override
	public void SendMessage(int stateChange) {
		// TODO Auto-generated method stub
		this.GetMediator().GetMessage(stateChange, this.name);
	}

	public void StartCoffee() {
		System.out.println("It's time to startcoffee!");
	}

	public void FinishCoffee() {

		System.out.println("After 5 minutes!");
		System.out.println("Coffee is ok!");
		SendMessage(0);
	}
}
