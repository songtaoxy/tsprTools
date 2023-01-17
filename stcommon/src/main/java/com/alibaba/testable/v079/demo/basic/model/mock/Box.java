package com.alibaba.testable.v079.demo.basic.model.mock;

abstract public class Box {

    protected String data;

    abstract public void put(String something);

    public String get() {
        return data;
    }

}
