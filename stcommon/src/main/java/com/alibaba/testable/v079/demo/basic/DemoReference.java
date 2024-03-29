package com.alibaba.testable.v079.demo.basic;

import com.alibaba.testable.v079.demo.basic.model.mock.BlackBox;
import com.alibaba.testable.v079.demo.basic.model.mock.Box;
import com.alibaba.testable.v079.demo.basic.model.mock.Color;

/**
 * 演示父类变量引用子类对象时的Mock场景
 * Demonstrate scenario of mocking method from sub-type object referred by parent-type variable
 */
public class DemoReference {

    /**
     * call method overridden by subclass via parent class variable
     */
    public Box putIntoBox() {
        Box box = new BlackBox("");
        box.put("data");
        return box;
    }

    /**
     * call method overridden by subclass via subclass variable
     */
    public BlackBox putIntoBlackBox() {
        BlackBox box = new BlackBox("");
        box.put("data");
        return box;
    }

    /**
     * call method defined in parent class via parent class variable
     */
    public String getFromBox() {
        Box box = new BlackBox("data");
        return box.get();
    }

    /**
     * call method defined in parent class via subclass variable
     */
    public String getFromBlackBox() {
        BlackBox box = new BlackBox("data");
        return box.get();
    }

    /**
     * call method defined in interface via interface variable
     */
    public String getColorViaColor() {
        Color color = new BlackBox("");
        return color.getColor();
    }

    /**
     * call method defined in interface via subclass variable
     */
    public String getColorViaBox() {
        BlackBox box = new BlackBox("");
        return box.getColor();
    }

    /**
     * call method defined in interface via subclass variable
     */
    public String getColorIdxViaColor() {
        Color color = new BlackBox("");
        return color.getColorIndex();
    }
}
