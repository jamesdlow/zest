package com.jameslow.zest;

import java.awt.event.*;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import com.jameslow.*;
import com.jameslow.gui.*;
import com.jameslow.pulp.*;

public class DragableTable extends Droppable implements ActionListener {
	protected JTable table;
	protected DefaultTableModel model;
	protected Map<String,Song> list;
	private JMenuItem add,remove,removeall,edit,show,exportfile,exportword,youtube,preview,upload,copy;
	protected ZestWindow zestwindow;
	public static final int FILE_COLUMN = 0;
	public static final int FIRSTLINE_COLUMN = 1;
	public static final int KEY_COLUMN = 2;
	public static final int ALTKEY_COLUMN = 3;
	public static final int MYKEY_COLUMN = 4;
	public static final int FOLDERYEAR_COLUMN = 5;
	public static final int LISTYEAR_COLUMN = 3;
	
	public DragableTable(ZestWindow zestwindow, boolean allowadd) {
		this.zestwindow = zestwindow;
		final DragableTable hacktable = this;
		if (allowadd) {
			table = new JTable() {
				public TableCellEditor getCellEditor(int row, int column) {
					if (isCellEditable(row, column)) {
						Song song = getSong(row);
						String[] keysandnotes = new String[Theory.NOTES_FLATS.length];
						String highnote = song.getPropertyValue(SectionType.HIGHNOTE);
						String key = song.getPropertyValue(SectionType.KEY);
						for (int i = 0; i < keysandnotes.length; i++) {
							keysandnotes[i] = makenotecell(key,highnote,Theory.NOTES_FLATS[i]);
						}
						JComboBox combobox = new JComboBox(keysandnotes);
						DefaultCellEditor editor = new DefaultCellEditor(combobox);
						return editor;
					} else {
						return super.getCellEditor(row,column);
					}
				}
				public String getToolTipText(MouseEvent e) {
					String tip = null;
					java.awt.Point p = e.getPoint();
					int rowIndex = rowAtPoint(p);
					if (rowIndex >= 0) {
						Song song = getSong(rowIndex);
						List<SectionProperty> props = song.getProperties();
						for (SectionProperty prop : props) {
							if (tip == null) {
								tip = "<html>"+prop.toString();
							} else {
								tip = tip + "<br>" + prop.toString();	
							}
						}
						if (tip != null) {
							String words = song.getFirstLine().trim();
							if (words.length() > 0) {
								tip = tip + "<br>First Line: " + words;
							}
							tip = tip+"</html>";
						}
					}
					return tip;
				}
			};
		} else {
			table = new SortableTable() {
				public String getToolTipText(MouseEvent e) {
					String tip = null;
					java.awt.Point p = e.getPoint();
					int rowIndex = rowAtPoint(p);
					if (rowIndex >= 0) {
						Song song = getSong(rowIndex);
						List<SectionProperty> props = song.getProperties();
						for (SectionProperty prop : props) {
							if (tip == null) {
								tip = "<html>"+prop.toString();
							} else {
								tip = tip + "<br>" + prop.toString();	
							}
						}
						if (tip != null) {
							String words = song.getFirstLine().trim();
							if (words.length() > 0) {
								tip = tip + "<br>First Line: " + words;
							}
							String filter = hacktable.getFilter();
							if (!MiscUtils.isblank(filter)) {
								String[] needles = MiscUtils.searchSplit(filter);
								boolean found = false;
								boolean firsttime = true;
								for (Section sect : song.getSections()) {
									for (Line line :sect.getLines()) {
										found = MiscUtils.match(needles,line.getWords());
										if (found) {
											if (firsttime) {
												firsttime = false;
												tip = tip + "<br><br>Matching lines:";
											}
											tip = tip + "<br>" + line.getWords();
										}
									}
								}
							}
							tip = tip+"</html>";
						}
					}
					return tip;
				}
				public void postSort() {
					TableColumn column = null;
					for (int i = 0; i < table.getColumnCount(); i++) {
						column = table.getColumnModel().getColumn(i);
						if (i == FILE_COLUMN || i == FIRSTLINE_COLUMN) {
							column.setPreferredWidth(250);
						} else if (i == FOLDERYEAR_COLUMN) {
							column.setPreferredWidth(40);
						} else {
							column.setPreferredWidth(70);
						}
					}
				}
			};
		}
		//table.setUI(new com.explodingpixels.macwidgets.plaf.ITunesTableUI());
		table.getTableHeader().setReorderingAllowed(false);
		if (Main.OS().javaVersion('1', '6')) {
			table.setFillsViewportHeight(true);
		}
		ToolTipManager instance = ToolTipManager.sharedInstance();
		instance.setDismissDelay(4000);
		instance.setInitialDelay(50);
		instance.setReshowDelay(50);
		//table.setFillsViewportHeight(true); 
		init(table);
		setAllowAdd(allowadd);
		createMenu();
	}
	public String getFilter() {
		return "";
	}
	public static String makenotecell(String key, String highnote) {
		return key + (MiscUtils.isblank(highnote) ? "" : " (" + highnote + ")");
	}
	public static String makenotecell(String key, String highnote, String newkey) {
		if (MiscUtils.isblank(newkey)) {
			return makenotecell(key,highnote);
		} else {
			if (MiscUtils.isblank(highnote)) {
				return makenotecell(newkey,null);
			} else {
				try {
					return makenotecell(newkey,(new Note(highnote)).transpose(key, newkey).getString(Theory.isSharpKey(new Note(newkey))));
				} catch (Exception e) {
					return makenotecell(newkey,null);
				}
			}
		}
	}
	public String getKey(int row) {
		return getKey(row,KEY_COLUMN);
	}
	public String getKey(int row, int col) {
		return ((String) table.getModel().getValueAt(row, col)).split(" ")[0];
	}
	public TableModel getModel() {
		return model;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			if (getSelectedIndex() >= 0) {
				for(int i : getSelectedIndices()) {
					zestwindow.addFilePath(getFilepath(i));
				}
			}
		} else if (e.getSource() == remove) {
			if (getSelectedIndex() >= 0) {
				removeSelectedFilepath();
			}
		} else if (e.getSource() == removeall) {
			removeAllFilepaths();
		} else if (e.getSource() == preview) {
			if (getSelectedIndex() >= 0) {
				preview();
			}
		} else if (e.getSource() == show) {
			if (getSelectedIndex() >= 0) {
				Main.OS().showFile(getSelectedFilePath());
			}
		} else if (e.getSource() == edit) {
			if (getSelectedIndex() >= 0) {
				Main.OS().openFile(getSelectedFilePath());
			}
		} else if (e.getSource() == youtube) {
			if (getSelectedIndex() >= 0) {
				try {
					Song song = Song.parse(getSelectedFilePath());
					String url = song.getPropertyValue(SectionType.YOUTUBE);
					if (url != null && "".compareTo(url) != 0) {
						Main.OS().openURL(url);
					}
				} catch (IOException e1) {
					Main.Logger().warning("Could not parse "+getSelectedFilePath());
				}
			}
		} else if (e.getSource() == exportfile) {
			zestwindow.export(false);
		} else if (e.getSource() == exportword) {
			zestwindow.export(true);
		} else if (e.getSource() == upload) {
			zestwindow.uploadSongs();
		} else if (e.getSource() == copy) {
			zestwindow.copyClipboard();
		}
	}
	public void preview() {
		zestwindow.preview(getSong(getSelectedIndex(),true));
	}
	public Song getSong() {
		return getSong(getSelectedIndex());
	}
	public Song getSong(int index) {
		return getSong(index,false);
	}
	public Song getSong(int index, boolean transpose) {
		try {
			String filepath = getFilepath(index);
			Song song = Song.parse(filepath);
			if (transpose) {
				String key = song.getPropertyValue(SectionType.KEY);
				if (MiscUtils.isblank(key)) {
					return song;
				} else {
					return song.transpose(key,getKey(index),true);
				}
			} else {
				return song;
			}
		} catch (IOException e) {
			Main.Logger().warning("Could get song index "+index +":"+ e.getMessage());
			return null;
		}
	}
	private void createMenu() {
		List<JMenuItem> list = new ArrayList<JMenuItem>();
		if (!getAllowAdd()) {
			add = new JMenuItem("Add");
			add.addActionListener(this);
			list.add(add);
		} else {
			remove = new JMenuItem("Remove");
			remove.addActionListener(this);
			list.add(remove);
			
			removeall = new JMenuItem("Remove All");
			removeall.addActionListener(this);
			list.add(removeall);
		}
		preview = new JMenuItem("Preview Song");
		preview.addActionListener(this);
		list.add(preview);
		edit = new JMenuItem("Edit File");
		edit.addActionListener(this);
		list.add(edit);
		show = new JMenuItem("Show File");
		show.addActionListener(this);
		list.add(show);
		youtube = new JMenuItem("YouTube");
		youtube.addActionListener(this);
		list.add(youtube);
		
		if (getAllowAdd()) {
			exportfile = new JMenuItem("Export To File");
			exportfile.addActionListener(this);
			list.add(exportfile);
			exportword = new JMenuItem("Export To Word");
			exportword.addActionListener(this);
			list.add(exportword);
			upload = new JMenuItem("Upload To Database");
			upload.addActionListener(this);
			list.add(upload);
			copy = new JMenuItem("Copy To Clipboard");
			copy.addActionListener(this);
			list.add(copy);
		}
		GUIUtils.addPopupListener(this.getComponent(), list);
	}
}
