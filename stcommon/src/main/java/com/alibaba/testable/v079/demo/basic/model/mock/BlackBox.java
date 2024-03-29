package com.alibaba.testable.v079.demo.basic.model.mock;

public class BlackBox extends Box implements Color {

    public BlackBox(String data) {
        this.data = data;
    }

    public static BlackBox secretBox() {
        return new BlackBox("secret");
    }

    @Override
    public void put(String something) {
        data = something;
    }

    @Override
    public String getColor() {
        return "black";
    }

    @Override
    public String getColorIndex() {
        return "idx";
    }
}
