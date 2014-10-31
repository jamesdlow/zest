package com.jameslow.zest;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

import com.google.gdata.util.AuthenticationException;
import com.itseasy.rtf.RTFDocument;
import com.jameslow.*;
import com.jameslow.gui.*;
import com.jameslow.pulp.*;
import com.jameslow.zest.util.*;

public class ZestWindow extends MainWindow {
	private JPanel left, right;
	private JSplitPane pane;
	private JScrollPane folderscroll,songlistscroll;
	protected FolderDragableTable folder = new FolderDragableTable(this);
	protected SongListDragableTable songlist = new SongListDragableTable(this);
	private SearchText search;
	protected ZestSettings settings;
	private Map<String,Integer> songhistory = new HashMap<String,Integer>();
	
	public ZestWindow() {
		super(true);
		settings = (ZestSettings) Main.Settings();
		updateFromSVN(true);
		left = new JPanel();
			left.setLayout(new BorderLayout());
			search = new SearchText(true,folder);
			left.add(search, BorderLayout.NORTH);
			folderscroll = new JScrollPane(folder.getComponent(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			left.add(folderscroll, BorderLayout.CENTER);
		right = new JPanel();
			right.setLayout(new BorderLayout());
			songlistscroll = new JScrollPane(songlist.getComponent(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			right.add(songlistscroll,BorderLayout.CENTER);
		pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
			pane.setDividerSize(3);
			pane.setContinuousLayout(true);
			pane.setResizeWeight(0.5);
		add(pane, BorderLayout.CENTER);
		setFolder();
		getSongs(false);
	}
	public void postLoad() {
		setDividerLocation(0.5);
	};
	public class refreshFolder extends AbstractAction {
		public refreshFolder(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			setFolder();
		}
	}
	public class downloadFiles extends AbstractAction {
		public downloadFiles(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			updateFromSVN(false);
			setFolder();
		}
	}
	public class resetStyles extends AbstractAction {
		public resetStyles(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			settings.setDefaultStyle();
			settings.saveSettings();
		}	
	}
	public class getSongsAction extends AbstractAction {
		public getSongsAction(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			getSongs(true);
		}	
	}
	public void createFileMenu(JMenu fileMenu) {
		int shortcutKeyMask = Main.OS().shortCutKey();
		fileMenu.add(new JMenuItem(new refreshFolder("Refresh Folder",KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcutKeyMask))));
		fileMenu.add(new JMenuItem(new downloadFiles("Download Remote",KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutKeyMask))));
		fileMenu.add(new JMenuItem(new resetStyles("Reset Styles",KeyStroke.getKeyStroke(KeyEvent.VK_T, shortcutKeyMask))));
		fileMenu.add(new JMenuItem(new getSongsAction("Get Song History",KeyStroke.getKeyStroke(KeyEvent.VK_G, shortcutKeyMask))));
	}
	public void setDividerLocation(double proportion) {
		pane.setDividerLocation(proportion);
	}
	public void setFolder() {
		folder.setFolder(settings.sourcefolder, ".txt");
		search.filterText();
	}
	public void addFilePath(int index) {
		addFilePath(folder.getFilepath(index));
	}
	public void addFilePath(String filepath) {
		songlist.addFilepath(filepath,songlist.getSize());
	}
	public void export(boolean open) {
		final File file = Main.OS().openFileDialog(this,true);
		if (file != null) {
			Page page = new Page(((ZestSettings) Main.Settings()).pagesizes.getSize("A4"), new Margins(12.7,12.7,12.7,12.7,0.5), true, 2);
			RTFDocument rtf = new RTFDocument(page.getSRWPageDefinition()); 
			SongList songs = new SongList();
			songs.setFormats(((ZestSettings) Main.Settings()).getCurrentStyle());
			for (int i = 0; i < songlist.getFilepaths().length; i++) {
				try {
					songs.writeSong(songlist.getSong(i,true), new RTFSongWriter(rtf));
				} catch (Exception e) {
					Main.Logger().warning("Could not parse "+songlist.getFilepath(i));
				}
			}
			String filename = file.getAbsoluteFile() + Main.OS().fileSeparator() + "Zest-" + (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + ".rtf";
			try {
				rtf.save(new File(filename));
				if (open) {
					Main.OS().openFile(filename);
				}
			} catch (IOException e) {
				Main.Logger().warning("Could not export file"+e.getMessage());
			}
			if (!MiscUtils.isblank(settings.googlekey) && !MiscUtils.isblank(settings.googlesheet) && !MiscUtils.isblank(settings.googleusername) && !MiscUtils.isblank(settings.googlepassword)) {
				int n = JOptionPane.showConfirmDialog(this,"Would you like to upload the songlist now?","Upload Songs?",JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					uploadSongs();
				}
			}
		}
	}
	public void copyClipboard() {
		SongList songs = new SongList();
		songs.setFormats(((ZestSettings) Main.Settings()).getCurrentStyle());
		StringSongWriter songdata = new StringSongWriter();
		for (int i = 0; i < songlist.getFilepaths().length; i++) {
			try {
				songs.writeSong(songlist.getSong(i,true), songdata);
			} catch (Exception e) {
				Main.Logger().warning("Could not parse "+songlist.getFilepath(i));
			}
		}
		TextTransfer textTransfer = new TextTransfer();
		textTransfer.setClipboardContents(songdata.getText());
	}
	public void preview(Song song) {
		Page page = new Page(((ZestSettings) Main.Settings()).pagesizes.getSize("A4"), new Margins(12.7,12.7,12.7,12.7,0.5), true, 2);
		PageWindow pagewin = new PageWindow(page);
		pagewin.setSong(song);
		pagewin.setVisible(true);
	}
	public WindowSettings getDefaultWindowSettings() {
		return new WindowSettings(640,480,0,0,true,JFrame.NORMAL);
	}
	public void updateFromSVN(boolean silent) {
		try {
			if (settings.svnurl.length() > 0) {
				SVNClass.updateOrCheckout(new File(settings.sourcefolder), settings.svnurl, settings.svnusername, settings.svnpassword);
			} else {
				if (!silent) {
					JOptionPane.showMessageDialog(this, "Please set a url in preferences.");
				}
			}
		} catch (Exception e) {
			String msg = "Could not update from SVN: "+e.getMessage();
			if (!silent) {
				JOptionPane.showMessageDialog(this, msg);
			}
			Main.Logger().warning(msg);
		}
	}
	public void uploadSongs() {
		final ZestWindow win = this;
		if (!MiscUtils.isblank(settings.googlekey) && !MiscUtils.isblank(settings.googlesheet) && !MiscUtils.isblank(settings.googleusername) && !MiscUtils.isblank(settings.googlepassword)) {
			UploadWindow window = new UploadWindow(this);
		} else {
			Main.showLogError("Please enter Google settings.","Upload Error",null,win);
		}
	}
	public void getSongs(final boolean showerror) {
		final ZestWindow win = this;
		if (!MiscUtils.isblank(settings.googlekey) && !MiscUtils.isblank(settings.googlesheet) && !MiscUtils.isblank(settings.googleusername) && !MiscUtils.isblank(settings.googlepassword)) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						GoogleClass google = new GoogleClass(settings.googlekey,settings.googlesheet,settings.googleusername,settings.googlepassword);
						songhistory = google.getSongs();
						//for (Map.Entry<String, Integer> entry : songhistory.entrySet()) {
						//}
						win.setFolder();
					} catch (Exception e) {
						Main.showLogError("Error getting spreadsheet, please check your settings.","Download Error",e,win);
					}
				}
			});
			thread.start();
		} else {
			if (showerror) {
				Main.showLogError("Please enter Google settings.","Download Error",null,win);
			}
		}
	}
	public int getCount(String filename) {
		String song = FileUtils.stripExt(filename);
		if (songhistory.containsKey(song)) {
			return songhistory.get(song);
		}
		return 0;
	}
}