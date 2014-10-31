package com.jameslow.zest;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import com.jameslow.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class GoogleClass {
	//http://code.google.com/apis/spreadsheets/data/3.0/developers_guide_java.html#ListFeeds
	private static String APP_CODE = "com.jameslow-zest-1.0";
	private SpreadsheetService service = null;
	private String key,sheet,username;
	
	public GoogleClass(String key, String sheet, String username, String password) throws AuthenticationException {
		this.key = key;
		this.sheet = sheet;
		this.username = username;
		service = new SpreadsheetService(APP_CODE);
		service.setUserCredentials(username,password);
	}
	private URL getListFeedURL() throws IOException, ServiceException {
		URL worksheetFeedUrl = new URL("https://spreadsheets.google.com/feeds/worksheets/"+key+"/private/full");
		WorksheetFeed worksheetFeed = service.getFeed(worksheetFeedUrl, WorksheetFeed.class);
		for (WorksheetEntry worksheet : worksheetFeed.getEntries()) {
			if (sheet.compareTo(worksheet.getTitle().getPlainText()) == 0) {
				return worksheet.getListFeedUrl();
			}
		}
		return null;
	}
	public Map<String,Integer> getSongs() throws IOException, ServiceException {
		Calendar calyear = Calendar.getInstance();
		calyear.add(Calendar.YEAR, -1);
		long exceldate = (long) MiscUtils.excelDate(calyear);
		
		ListQuery query = new ListQuery(getListFeedURL());
		query.setSpreadsheetQuery("date > " + exceldate);
		ListFeed feed = service.query(query, ListFeed.class);
		Map<String,Integer> result = new HashMap<String,Integer>();
		for (ListEntry entry : feed.getEntries()) {
			String title = entry.getTitle().getPlainText();
			if (result.containsKey(title)) {
				result.put(title, result.get(title) + 1);
			} else {
				result.put(title, 1);
			}
		}
		return result;
	}
	public void updateSongs(Calendar date, List<String> songs) throws IOException, ServiceException {
		updateSongs(date,songs,"");
	}
	public void updateSongs(Calendar date, List<String> songs, String other) throws IOException, ServiceException {
		String exceldate = ""+MiscUtils.excelDate(date);
		URL listFeedUrl = getListFeedURL();
		for (String song : songs) {
			ListEntry newEntry = new ListEntry();
			newEntry.getCustomElements().setValueLocal("title", song);
			newEntry.getCustomElements().setValueLocal("date", exceldate);
			newEntry.getCustomElements().setValueLocal("user", username);
			if (!MiscUtils.isblank(other)) {
				newEntry.getCustomElements().setValueLocal("other", other);
			}
			service.insert(listFeedUrl, newEntry);
		}
	}
}