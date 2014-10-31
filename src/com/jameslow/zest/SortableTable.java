package com.jameslow.zest;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class SortableTable extends JTable {
	private int lastcolumn = 0;
	private boolean lastorder = true;
	
	public SortableTable() {
		getTableHeader().addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				JTableHeader header = (JTableHeader) e.getSource();
				int column = header.columnAtPoint(e.getPoint());
				sortAllRowsByReverse(column);
		 	}
		 	public void mouseEntered(MouseEvent e) {}
		 	public void mouseExited(MouseEvent e) {}
		 	public void mousePressed(MouseEvent e) {}
		 	public void mouseReleased(MouseEvent e) {}
		});
	}
	
	public void reset() {
		lastcolumn = 0;
		lastorder = true;
	}
	
	public void setModel(DefaultTableModel dataModel) {
		super.setModel(dataModel);
		setAutoCreateColumnsFromModel(false);
	}
	
	public DefaultTableModel getModel() {
		return (DefaultTableModel) super.getModel();
	}
	
	public void sortAllRowsByReverse(int sortcolumn) {
		if (lastcolumn == sortcolumn) {
			lastorder = !lastorder;
		} else {
			lastorder = true;
		}
		sortAllRowsBy(sortcolumn);
	}
	public void sortAllRowsBy() {
		sortAllRowsBy(lastcolumn);
	}
	public void sortAllRowsBy(int sortcolumn) {
		DefaultTableModel model = getModel();
		
		lastcolumn = sortcolumn;
		Vector data = model.getDataVector();
		Collections.sort(data, new ColumnSorter(sortcolumn, lastorder));
		
		for (int i = 0; i < model.getColumnCount(); i++ ) {
			String name = (String) getColumnModel().getColumn(i).getHeaderValue();
			if (name.endsWith(" v") || name.endsWith(" ^")) {
				name = name.substring(0,name.length()-2);
			}
			
			if (i == sortcolumn) {
				if (lastorder) {
					name = name + " v";
				} else {
					name = name + " ^";
				}
			}
			getColumnModel().getColumn(i).setHeaderValue(name);
		}
		getTableHeader().resizeAndRepaint();
		model.fireTableStructureChanged();
		postSort();
	}
	public void postSort() {}
	
    // This comparator is used to sort vectors of data
    public class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;
        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }
        public int compare(Object a, Object b) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);
    
            // Treat empty strains like nulls
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
    
            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable)o1).compareTo(o2);
                } else {
                    return ((Comparable)o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    }
}
