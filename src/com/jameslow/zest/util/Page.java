package com.jameslow.zest.util;

import com.itseasy.rtf.text.PageDefinition;
import com.jameslow.Main;

public class Page {
	public static double INCH_PER_MM = 25.4;
	public static int STANDARD_MONITOR_PPI = 72;
	public static int mm_to_pixel(double length) {
		return (int) (length/INCH_PER_MM * STANDARD_MONITOR_PPI); 
	}
	public static PageDimension mm_to_pixel(PageDimension dimension) {
		return mm_to_pixel(dimension.getWidth(),dimension.getHeight());
	}
	public static PageDimension mm_to_pixel(double width, double height) {
		return new PageDimension(mm_to_pixel(width),mm_to_pixel(height));
	}
	public static double ratio(double width, double height) {
		return height/width;
	}
	
	private Margins margins;
	private PageSize pagesize;
	private int columns;
	private boolean orientation_portrait;
	
	public Page (PageSize pagesize, Margins margins) {
		this(pagesize,margins,true);
	}
	public Page (PageSize pagesize, Margins margins, boolean orientation_portrait) {
		this(pagesize,margins,orientation_portrait,1);
	}
	public Page (PageSize pagesize, Margins margins, boolean orientation_portrait,int columns) {
		this.margins = margins;
		this.pagesize = pagesize;
		this.columns = columns;
	}
	public boolean validateWidth() {
		return innerWidth() - columnSpaceWidth()  > 0;
	}
	public boolean validateHeight() {
		return innerHeight() > 0;
	}
	public boolean validatePage() {
		return validateHeight() && validateWidth();
	}
	public boolean isOrientationPortrait() {
		return orientation_portrait;
	}
	public int getColumns() {
		return columns;
	}
	public Margins getMargins() {
		return margins;
	}
	public PageSize getPageSize() {
		return pagesize;
	}
	public Margins getMargins(double zoom) {
		return new Margins(margins.left*zoom,margins.right*zoom,margins.top*zoom,margins.bottom*zoom,margins.columns*zoom);
	}
	public double innerWidth() {
		return pagesize.width - margins.left - margins.right;
	}
	public double innerWidth(double zoom) {
		return innerWidth()*zoom;
	}
	public double innerHeight() {
		return pagesize.height - margins.top - margins.bottom;
	}
	public double innerHeight(double zoom) {
		return innerHeight()*zoom;
	}
	public double columnSpaceWidth() {
		return (columns-1) * margins.columns;
	}
	public double columnSpaceWidth(double zoom) {
		return columnSpaceWidth()*zoom;
	}
	public double columnWidth() {
		return (innerWidth() - columnSpaceWidth()) / columns; 
	}
	public double columnWidth(double zoom) {
		return columnWidth()*zoom;
	}
	public double getRatio() {
		return ratio(pagesize.width,pagesize.height);
	}
	public PageDimension resizeToScreen() {
		return mm_to_pixel(pagesize.width,pagesize.height);
	}
	public PageDimension resizeToScreen(double zoom) {
		return mm_to_pixel(pagesize.width*zoom,pagesize.height*zoom);
	}
	public PageDimension resizeToRatio(PageDimension dimension, boolean bigger) {
		return resizeToRatio(dimension.width,dimension.height,bigger);
	}
	public PageDimension resizeToRatio(double width, double height, boolean bigger) {
		double current = getRatio();
		if (bigger) {
			return (ratio(width,height) > current ? new PageDimension(height/current,height) : new PageDimension(width,width*current));
		} else {
			return (ratio(width,height) < current ? new PageDimension(height/current,height) : new PageDimension(width,width*current));
		}
	}
	public com.itseasy.rtf.text.PageDefinition getSRWPageDefinition() {
		return new com.itseasy.rtf.text.PageDefinition(pagesize.getSRWPageSize(), margins.left, margins.right, margins.top, margins.bottom, columns, margins.columns);
	}
}