package com.jameslow.zest.util;

import java.awt.*;

public class PageDimension {
	protected double width;
	protected double height;
	public PageDimension(double width, double height) {
		this.width = width;
		this.height = height;
	}
	public double getWidth() {
		return width;
	}
	public double getHeight() {
		return height;
	}
	public int getRoundedWidth() {
		return (int) width;
	}
	public int getRoundedHeight() {
		return (int) height;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public Dimension getDimension() {
		return new Dimension(getRoundedWidth(),getRoundedHeight());
	}
}
