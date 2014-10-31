package com.jameslow.zest;

import com.jameslow.*;

public class Zest extends Main {
	public Zest(String args[]) {
		super(args,null,null,ZestSettings.class.getName(),ZestWindow.class.getName(),null,null,ZestPref.class.getName());
		/*
		try {
			GoogleClass google = new GoogleClass("0ApgrR7SHGmmEdGZQVVVfYWQ1SXlrbVpkMVhUejlxblE","History","","");
			google.getSongs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
	public static void main(String args[]) {
		instance = new Zest(args);
	}
}
