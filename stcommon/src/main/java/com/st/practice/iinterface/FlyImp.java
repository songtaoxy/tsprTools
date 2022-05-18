package com.st.practice.iinterface;

/**
 * @author: st
 * @date: 2022/5/18 09:59
 * @version: 1.0
 * @description:
 */
public class FlyImp extends FlyParent implements Fly01, Fly02 {
	@Override
	public void fly() {
		System.out.println("fly ....");
	}

	public void callParentFly() {
		super.fly();
		fly();
	}


	public void callEat() {
		eat();
		super.eat();
	}

	public static void main(String[] args) {
		FlyImp flyImp = new FlyImp();
		flyImp.fly();
		flyImp.callParentFly();

		flyImp.callEat();

		FlyParent flyParent = new FlyParent();
		flyParent.fly();
	}


}
