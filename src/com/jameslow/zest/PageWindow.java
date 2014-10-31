package com.jameslow.zest;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import com.jameslow.*;
import com.jameslow.pulp.*;
import com.jameslow.zest.util.*;
import com.itseasy.rtf.*;
import com.itseasy.rtf.text.PageDefinition;
import com.itseasy.rtf.text.PageSize;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.nodes.PStyledText;
import edu.umd.cs.piccolox.swing.*;

public class PageWindow extends MainWindow implements ComponentListener {
	private int resizecount = 0;
	private Page page;
	private PStyledText rootnode;
	private double scale;
	private Dimension lastsize;
	private ArrayList<PCanvas> columns = new ArrayList<PCanvas>();
	
	public PageWindow(Page page) {
		setPage(page);
		addComponentListener(this);
		setLastSize();
		
		setLayout(null);
		PageDimension d = getRatioSize();
    	scale = d.getWidth() / page.resizeToScreen().getWidth();
    	Margins margins = page.getMargins(scale);
		for (int i = 0; i < page.getColumns(); i++) {
			PCanvas canvas = new PCanvas();
			columns.add(canvas);
			//System.out.println(""+i+": "+Page.mm_to_pixel(margins.left + i*(margins.columns + page.columnWidth(scale)))+", "+Page.mm_to_pixel(margins.top)+", "+Page.mm_to_pixel(page.columnWidth(scale))+", "+Page.mm_to_pixel(page.innerHeight(scale)));
			canvas.setBounds(Page.mm_to_pixel(margins.left + i*(margins.columns + page.columnWidth(scale))), Page.mm_to_pixel(margins.top), Page.mm_to_pixel(page.columnWidth(scale)), Page.mm_to_pixel(page.innerHeight(scale)));
			add(canvas);
		}
	}
	public void setCanvas() {
		
	}
	public void resizeColumns(double scale) {
		Margins margins = page.getMargins(scale);
		int i = 0;
		for (PCanvas canvas : columns) {
			canvas.setBounds(Page.mm_to_pixel(margins.left + i*(margins.columns + page.columnWidth(scale))), Page.mm_to_pixel(margins.top), Page.mm_to_pixel(page.columnWidth(scale)), Page.mm_to_pixel(page.innerHeight(scale)));
			i++;
		}
	}
	public void setSong(Song song) {
		PCanvas canvas = columns.get(0);
		SongList songlist = new SongList();
		songlist.setFormats(((ZestSettings) Main.Settings()).getCurrentStyle());
		
		StyleContext sc = new StyleContext();
	    final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
	    JTextPane pane = new JTextPane(doc);

	    // Create and add the style
	    final Style heading2Style = sc.addStyle("Heading2", null);
	    heading2Style.addAttribute(StyleConstants.Foreground, Color.red);
	    heading2Style.addAttribute(StyleConstants.FontSize, new Integer(12));
	    heading2Style.addAttribute(StyleConstants.FontFamily, "serif");
	    heading2Style.addAttribute(StyleConstants.Bold, new Boolean(true));
	    try {
	    	//TODO: might need to redraw as initial seems to be the wrong size
	    	songlist.writeSong(song, new DocumentSongWriter(doc,sc));
			//doc.insertString(0, songlist.getSong(song), null);
			//doc.insertString(doc.getLength(), "RAH", heading2Style);
			//doc.setParagraphAttributes(0, 1, heading2Style, false);
			//doc.setStyle
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    //doc.setParagraphAttributes(0, 1, heading2Style, false);
		
		rootnode = new PStyledText();
		rootnode.setDocument(doc);
		canvas.getLayer().addChild(rootnode);
		//PScrollPane pane = new PScrollPane(canvas);
		rootnode.setChildrenPickable(false);
		canvas.setPanEventHandler(null);
		
		/*
		   public PNodeText(String text) {
	                sas = new SimpleAttributeSet();
	                data = new DefaultStyledDocument();
	        
	                try {
	                        data.insertString(0, text, null);
	                } catch (Exception e) {
	                }
	
	                setPaint(Color.white);
	                setDocument(data);
	                setVisible(true);
	        
	        }
		 */
		/*
		JTextArea textarea = new JTextArea();
		SongList songlist = new SongList();
		textarea.append(songlist.getSong(song));
		JScrollPane scroller = new JScrollPane();
		scroller.getViewport().add(textarea);
		add(scroller,BorderLayout.CENTER);
		*/ 
	}
	public void readRTF () {
		RTFEditorKit rtf = new RTFEditorKit();
		JEditorPane editor = new JEditorPane();
		editor.setEditorKit(rtf);
		editor.setBackground (Color.white);
		
		// This text could be big so add a scroll pane
		JScrollPane scroller = new JScrollPane();
		scroller.getViewport().add(editor);
		add(scroller,BorderLayout.CENTER);

		// Load an RTF file into the editor
		try {
			FileInputStream fi = new FileInputStream("/Users/James/Documents/Temp/Software/rtf/columns.txt");
			rtf.read(fi,editor.getDocument(),0);
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		}
		catch(IOException e) {
			System.out.println("I/O error");
		}
		catch( BadLocationException e ) {
		}
	}
	@Override
	public String getDefaultTitle() {
		return "Page Preview";
	}

	@Override
	public WindowSettings getDefaultWindowSettings() {
		//Dimension d = pagesize.resizeToScreen();
		//return new WindowSettings(d.getRoundedWidth(),d.getRoundedHeight(),0,0,true);
		return new WindowSettings(595,842,0,0,true);
	}
	
	private void setLastSize() {
		lastsize = getInnerSize();
	}
	private void setPage(Page page) {
		this.page = page;
		//TODO: minimum displayable scale seems to be 0.459 although I'm not sure if this is dependent on font size
		//Dimension d = pagesize.resizeToScreen(0.459);
		//setPreferredSize(new java.awt.Dimension((int) Math.ceil(d.getWidth()),(int) Math.ceil(d.getHeight())));
	}
	
	private PageDimension getRatioSize() {
		Dimension d = getInnerSize();
		return page.resizeToRatio(d.getWidth(), d.getHeight(), d.getWidth() > lastsize.getWidth() || d.getHeight() > lastsize.getHeight());
	}
	public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentResized(ComponentEvent e) {
    	if (resizecount == 0) {
    		//TODO: add a minimum size when text stops displaying
    		//TODO: add maximum size when reaches bottom of screen
    		//TODO: account for border, different on windows etc.
    		resizecount = 2;
    		PageDimension d = getRatioSize();
	    	scale = d.getWidth() / page.resizeToScreen().getWidth();
	    	if (rootnode != null) {
	    		rootnode.setScale(scale);
	    	}
	    	lastsize = new Dimension(d.getRoundedWidth(),d.getRoundedHeight());
	    	setInnerSize(d.getDimension());
	    	resizeColumns(scale);
    	} else if (resizecount > 0) {
    		resizecount--;
    	}
    }
    public void componentShown(ComponentEvent e) {}
	public void postLoad() {}
}
