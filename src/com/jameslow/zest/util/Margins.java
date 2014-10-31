package com.jameslow.zest.util;

public class Margins {
	public double left;
	public double right;
	public double top;
	public double bottom;
	public double columns;
	public Margins(double left, double right, double top, double bottom) {
		this(left,right,top,bottom,0);
	}
	public Margins(double left, double right, double top, double bottom, double columns) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.columns = columns;
	}
}