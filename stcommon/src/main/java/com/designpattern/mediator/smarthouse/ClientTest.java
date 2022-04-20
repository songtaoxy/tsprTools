package com.designpattern.mediator.smarthouse;

public class ClientTest {

	public static void main(String[] args) throws NoSuchMethodException {
		//����һ���н��߶���
		Mediator mediator = new ConcreteMediator();

	Colleague c = 	new Colleague(mediator, "only for test") {
			@Override
			public void SendMessage(int stateChange) {
			}
		};
		
		//����Alarm ���Ҽ��뵽  ConcreteMediator �����HashMap
		Alarm alarm = new Alarm(mediator, "alarm");
		
		//������CoffeeMachine ���󣬲�  �Ҽ��뵽  ConcreteMediator �����HashMap
		CoffeeMachine coffeeMachine = new CoffeeMachine(mediator,
				"coffeeMachine");
		
		//���� Curtains , ��  �Ҽ��뵽  ConcreteMediator �����HashMap
		Curtains curtains = new Curtains(mediator, "curtains");
		TV tV = new TV(mediator, "TV");
		
		//�����ӷ�����Ϣ
		alarm.SendAlarm(0);
		coffeeMachine.FinishCoffee();
		alarm.SendAlarm(1);
	}

}
