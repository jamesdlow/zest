package com.jameslow.zest;

import com.itseasy.rtf.*;
import com.itseasy.rtf.text.*;
import com.jameslow.pulp.FontFormat;
import com.jameslow.pulp.SongWriter;
import com.jameslow.pulp.FontFormat.Align;

public class RTFSongWriter implements SongWriter {
	private RTFDocument doc;
	private Paragraph para;
	
	public RTFSongWriter(RTFDocument doc) {
		this.doc = doc;
	}
	/*
	public void write(String line, FontFormat fontformat) {
		//TextPart text = new TextPart
		int fontatt = TextPart.FORMAT_NORMAL;
		if (fontformat.font.isBold()) {
			fontatt = fontatt + TextPart.FORMAT_BOLD;
		}
		if (fontformat.font.isItalic()) {
			fontatt = fontatt + TextPart.FORMAT_ITALIC;
		}
		java.awt.Font font = fontformat.font;
        Paragraph text = new Paragraph(0, 0, font.getSize(), new Font(font.getName(),font.getFamily()), new TextPart(fontatt,line));
        if (fontformat.align == FontFormat.Align.CENTER) {
        	text.setAlignment(Paragraph.ALIGN_CENTER);
        } else if (fontformat.align == FontFormat.Align.JUSTIFIED) {
        	text.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        } else if (fontformat.align == FontFormat.Align.LEFT) {
        	text.setAlignment(Paragraph.ALIGN_LEFT);
        } else if (fontformat.align == FontFormat.Align.RIGHT) {
        	text.setAlignment(Paragraph.ALIGN_RIGHT);
        }
        //TextPart text2 = new Textp
		doc.addParagraph(text);
	}
	 */
	public void write(String line, FontFormat fontformat, boolean newline) {
		int fontatt = TextPart.FORMAT_NORMAL;
		if (fontformat.font.isBold()) {
			fontatt = fontatt + TextPart.FORMAT_BOLD;
		}
		if (fontformat.font.isItalic()) {
			fontatt = fontatt + TextPart.FORMAT_ITALIC;
		}
		
		String align = Paragraph.ALIGN_LEFT;
		if (fontformat.align == FontFormat.Align.CENTER) {
			align = Paragraph.ALIGN_CENTER;
        } else if (fontformat.align == FontFormat.Align.JUSTIFIED) {
        	align = Paragraph.ALIGN_JUSTIFIED;
        } else if (fontformat.align == FontFormat.Align.LEFT) {
        	align = Paragraph.ALIGN_LEFT;
        } else if (fontformat.align == FontFormat.Align.RIGHT) {
        	align = Paragraph.ALIGN_RIGHT;
        }
		
		java.awt.Font font = fontformat.font;
		TextPart text = new TextPart(fontatt, font.getSize(), new Font(font.getName(),font.getFamily()), line);
		if (para == null) {
			para = new Paragraph(0, 0, font.getSize(), new Font(font.getName(),font.getFamily()), new TextPart(""));
			doc.addParagraph(para);
		}
		para.addText(text);
		para.setAlignment(align);
		if (newline) {
			para = new Paragraph(0, 0, font.getSize(), new Font(font.getName(),font.getFamily()), new TextPart(""));
			doc.addParagraph(para);
		}
	}
}
