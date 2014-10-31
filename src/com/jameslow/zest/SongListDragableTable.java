package com.jameslow.zest;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.jameslow.*;
import com.jameslow.pulp.*;

public class SongListDragableTable extends DragableTable implements TableModelListener, MouseListener, KeyListener {
	protected DroppableListener listener = new DroppableListener() {
		private String[] getRow(String filepath) {
			String[] row = new String[LISTYEAR_COLUMN+1];
			row[FILE_COLUMN] = FileUtils.getFilename(filepath);
			try {
				Song song = Song.parse(filepath);
				row[FIRSTLINE_COLUMN] = song.getFirstLine().trim();
				String altkey = song.getPropertyValue(SectionType.ALTKEY);
				String key = song.getPropertyValue(SectionType.KEY);
				if (key == null) { key = ""; };
				if (zestwindow.settings.usemykeys) { 
					row[KEY_COLUMN] = makenotecell(key,song.getPropertyValue(SectionType.HIGHNOTE),zestwindow.settings.getMyKey(filepath,key));
				} else if (zestwindow.settings.usealtkey && altkey != null) {
					row[KEY_COLUMN] = makenotecell(key,song.getPropertyValue(SectionType.HIGHNOTE),altkey);
				} else {
					row[KEY_COLUMN] = makenotecell(key,song.getPropertyValue(SectionType.HIGHNOTE));
				}
				row[LISTYEAR_COLUMN] = ""+zestwindow.getCount(row[FILE_COLUMN]);
			} catch (IOException e) {
				Main.Logger().warning("Could not parse "+filepath);
			}
			return row;
		}
		public void addFilePath(String filepath) {
			model.addRow(getRow(filepath));
		}
		public void addFilePath(int index, String filepath) {
			model.insertRow(index, getRow(filepath));
		}
		public void rearrangeFilePath(int index, int to) {

		}
		public void removeFilePath(int index) {
			model.removeRow(index);
		}
	};
	public SongListDragableTable(ZestWindow zestwindow) {
		super(zestwindow,true);
		setAllowRearrage(true);
		listeners.add(listener);
		model = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return column == KEY_COLUMN;
			}
		};
		model.addColumn("Name");
		model.addColumn("First Line");
		model.addColumn("Key");
		model.addColumn("Year");
		table.setModel(model);
		model.addTableModelListener(this);
		model.fireTableStructureChanged();
		TableColumn column = null;
		for (int i = 0; i < table.getColumnCount(); i++) {
		    column = table.getColumnModel().getColumn(i);
		    if (i == FILE_COLUMN || i == FIRSTLINE_COLUMN) {
		        column.setPreferredWidth(150);
		    } else {
		    	column.setPreferredWidth(30);
		    }
		}
		table.addKeyListener(this);
		table.addMouseListener(this);
		/*
		//Not sure why this doesn't work, did a hack in DraggableTable
		TableColumn mykey = table.getColumnModel().getColumn(2);
		JComboBox combobox = new JComboBox(Theory.NOTES_FLATS);
		mykey.setCellEditor(new DefaultCellEditor(combobox));
		System.out.println(""+table.getColumnModel().getColumn(2).getCellEditor().getTableCellEditorComponent(table, "C", true, 0, 2));
		model.fireTableStructureChanged();
		*/
	}
	public void tableChanged(TableModelEvent e) {
		int col = e.getColumn();
		if ((zestwindow != null && zestwindow.settings != null && zestwindow.settings.usemykeys) && e.getType() == TableModelEvent.UPDATE && col == KEY_COLUMN) {
			int row = e.getFirstRow();
			zestwindow.settings.setMyKey(getFilepath(row), getKey(row));
		}
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			preview();
		}
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == 8) {
			int index = getSelectedIndex();
			if (index >= 0) {
				removeSelectedFilepath();
				int size = getSize();
				if (size > 0) {
					if (index == 0) {
						setSelectedIndex(0);
					} else if (index == size) {
						setSelectedIndex(index-1);
					} else {
						setSelectedIndex(index);
					}
				}
			}
		}
	}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}

}
