package com.jameslow.zest;

import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.jameslow.*;
import com.jameslow.pulp.SectionType;
import com.jameslow.pulp.SongList;
import com.toedter.calendar.JCalendar;

public class UploadWindow extends AbstractWindow implements MouseListener {
	private JCalendar calendar;
	private JButton cancel,ok;
	private ZestWindow zest;
	public UploadWindow(ZestWindow zest) {
		this.zest = zest;
		setLayout(null);
		calendar = new JCalendar();
		calendar.setLocale(java.util.Locale.ENGLISH);
		add(calendar);
		calendar.setBounds(5,3,270,200);
		
		cancel = new JButton("Cancel");
		add(cancel);
		cancel.setBounds(70,200,100,30);
		cancel.addMouseListener(this);
		ok = new JButton("OK");
		add(ok);
		ok.setBounds(170,200,100,30);
		ok.addMouseListener(this);
		
		setResizable(false);
		setDefaultBounds();
		setVisible(true);
	}
	public String getDefaultTitle() {
		return "Upload Songlist";
	}
	public WindowSettings getDefaultWindowSettings() {
		return new WindowSettings(280,260,0,0);
	}
	public void postLoad() {}
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == ok) {
			final UploadWindow win = this;
			final Calendar date = Calendar.getInstance();
			date.set(calendar.getYearChooser().getYear(), calendar.getMonthChooser().getMonth(), calendar.getDayChooser().getDay());
			final List<String> songs = new ArrayList<String>();
			for (int i = 0; i <zest.songlist.getSize(); i++) {
				//songs.add(zest.songlist.getSong(i).getPropertyValue(SectionType.TITLE));
				songs.add(FileUtils.stripExt(zest.songlist.getFilename(i)));
			}
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						GoogleClass google = new GoogleClass(zest.settings.googlekey,zest.settings.googlesheet,zest.settings.googleusername,zest.settings.googlepassword);
						google.updateSongs(date, songs);
					} catch (Exception e1) {
						Main.showLogError("Error writing to spreadsheet, please check your settings.","Upload Error",e1,win);
					}
					setVisible(false);
				}
			});
			thread.start();
		} else {
			setVisible(false);
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}
