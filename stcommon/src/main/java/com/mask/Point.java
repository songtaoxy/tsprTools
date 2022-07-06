package com.mask;

/**
 * @author: st
 * @date: 2022/7/6 19:15
 * @version: 1.0
 * @description:
 */
public class Point {
	private Integer xAxis;
	private Integer yAxis;

	public Point() {
		super();
	}

	public Point(Integer xAxis, Integer yAxis) {
		super();
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

	public Integer getxAxis() {
		return xAxis;
	}

	public void setxAxis(Integer xAxis) {
		this.xAxis = xAxis;
	}

	public Integer getyAxis() {
		return yAxis;
	}

	public void setyAxis(Integer yAxis) {
		this.yAxis = yAxis;
	}

	@Override
	public String toString() {
		return "Point [xAxis=" + xAxis + ", yAxis=" + yAxis + "]";
	}
}
