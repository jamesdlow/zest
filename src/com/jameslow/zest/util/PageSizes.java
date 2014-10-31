package com.jameslow.zest.util;

import java.io.*;
import java.util.*;
import com.jameslow.*;

public class PageSizes {
	private List<PageSize> sizes = new ArrayList<PageSize>();

	public PageSizes(Settings settings) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(settings.getResourceAsStream(settings.getProperty("zest.pagesizes"))));
			String s;
			while ((s = reader.readLine()) != null) {
				String[] split = s.split(",");
				try {
					sizes.add(new PageSize(split[0],Double.parseDouble(split[2]),Double.parseDouble(split[1])));
				} catch (IndexOutOfBoundsException e1) {
					Main.Logger().warning("Invalid record, not enough params in page sizes: " + s);
				} catch (NumberFormatException e2) {
					Main.Logger().warning("Invalid record, invliad number in page sizes: " + s);
				}
			}
		} catch (IOException e) {
			Main.Logger().warning("Could not load page sizes file: " + e.getMessage());
		}
	}
	public List<PageSize> getSizes() {
		return sizes;
	}
	public PageSize getSize(String name) {
		for (PageSize size : sizes) {
			if (name.compareTo(size.getName()) == 0) {
				return size;
			}
		}
		return null;
	}
	public void addSize(PageSize size) {
		sizes.add(size);
	}
}
