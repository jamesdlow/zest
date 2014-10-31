package com.jameslow.zest;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.jameslow.*;
import com.jameslow.gui.*;
import com.jameslow.pulp.*;

public class FolderDragableTable extends DragableTable implements SearchTextFilter, TableModelListener, MouseListener {
	private String folder;
	private String filter = "";
	
	public FolderDragableTable(ZestWindow zestwindow) {
		super(zestwindow,false);
		table.addMouseListener(this);
	}
	
	public String getFolder() {
		return folder;
	}
	//TODO: could change this so it filters the list and then adds to the filepaths using a droppable listenter
	public String getFilter() {
		return filter;
	}
	private void setTableModel(String str) {
		filter = str;
		model = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		model.addTableModelListener(this);
		model.addColumn("Name");
		model.addColumn("First Line");
		model.addColumn("Key");
		model.addColumn("Alt Key");
		model.addColumn("My Key");
		model.addColumn("Year");
		
		String[] needles = MiscUtils.searchSplit(str);
		for(Map.Entry<String, Song> e : list.entrySet()) {
			Song song = e.getValue();
			String title = e.getKey();
			String firstline = song.getFirstLine().trim();
			boolean found = false;
			if (str.length() > 0) {
				//TODO: beginnings of complete search, need to decide 2 things
				//1) Should we search for individual words in multiple lines
				//2) How do we best display to the user that is has this line
				for (SectionProperty prop : song.getProperties()) {
					found = MiscUtils.match(needles,prop.data);
					if (found) {
						break;
					}
				}
				if (!found) {
					for (Section sect : song.getSections()) {
						for (Line line :sect.getLines()) {
							found = MiscUtils.match(needles,line.getWords());
							if (found) {
								break;
							}
						}
						if (found) {
							break;
						}
					}
				}
			} else {
				found = true;
			}
			//if (str.length() == 0 || MiscUtils.match(needles,title) || MiscUtils.match(needles,firstline)) {
			if (found || MiscUtils.match(needles,title)) {
				String key = song.getPropertyValue(SectionType.KEY);
				if (key == null) { key = ""; }; 
				String altkey = song.getPropertyValue(SectionType.ALTKEY);
				String highnote = song.getPropertyValue(SectionType.HIGHNOTE);
				String mykeystart = key;
				if (zestwindow.settings.usealtkey) {
					mykeystart = altkey;
				}
				String mykey = zestwindow.settings.getMyKey(title, mykeystart);
				model.addRow(new Object[]{title,firstline,
					makenotecell(key,highnote),
					makenotecell(key,highnote,altkey),
					makenotecell(key,highnote,mykey),
					zestwindow.getCount(title)
				});
			}
		}
		table.setModel(model);
		model.fireTableStructureChanged();
		((SortableTable) table).postSort();
	}
	public void setFolder(String folder, String ext) {
		this.folder = folder;
		list = new LinkedHashMap<String,Song>();

		File dir = new File(folder);
		String[] files = dir.list();
		String[] exts = ext.split(",");
		for (int i=0; i<files.length; i++) {
			if (FileUtils.exts(files[i], exts)) {
				File file = new File(dir.getAbsolutePath() + Main.OS().fileSeparator() + files[i]);
				if (file.canRead() && file.isFile() && !file.isHidden()) {
					try {
						Song song = Song.parse(file.getAbsolutePath());
						list.put(file.getName(),song);
					} catch (IOException e) {
						Main.Logger().warning("Could not parse "+file.getAbsolutePath());
					}
				}
			}
		}
		setTableModel("");
	}
	public void doFilter(String text) {
		setTableModel(text.toUpperCase());
	}
	public void tableChanged(TableModelEvent e) {
		removeAllFilepaths();
		for (int i = 0; i < model.getRowCount(); i++) {
			addFilepath(folder + Main.OS().fileSeparator() + (String)model.getValueAt(i, 0));
		}
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			zestwindow.addFilePath(table.getSelectedRow());
		}
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}