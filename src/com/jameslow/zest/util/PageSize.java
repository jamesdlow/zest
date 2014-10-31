package com.jameslow.zest.util;

public class PageSize extends PageDimension {	
	private String name;
	public PageSize(String name, double width, double height) {
		super(height,width);
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public com.itseasy.rtf.text.PageSize getSRWPageSize() {
	    return new com.itseasy.rtf.text.PageSize(height,width);
	}
}
