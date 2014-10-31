package com.jameslow.zest;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import com.jameslow.*;

public class ZestPref extends PrefPanel implements ActionListener {
	protected ZestSettings settings;
	protected JButton folderbrowse, resetmykeys;
	protected JTextField folder, svnurl, svnusername, googlekey, googlesheet, googleusername;
	protected JPasswordField svnpassword, googlepassword;
	protected JComboBox fontsize, fontname, spacecombo;
	protected JCheckBox doubleline, usealtkey, usemykeys;
	
	public ZestPref() {
		super();
		setLayout(new GridLayout(14,2));
		Dimension size = new Dimension(320,340); 
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		
		settings = (ZestSettings) Main.Settings();
		
		folderbrowse = new JButton("Folder");
			folderbrowse.addActionListener(this);
		add(folderbrowse);
		folder = new JTextField();
			folder.setText(settings.sourcefolder);
			folder.setEditable(false);
		add(folder);
		
		Font font = settings.getDefaultFont();
		
		JLabel fontsizelabel = new JLabel("Font Size:");
		add(fontsizelabel);
		String[] fonts = new String[10];
		int selected = 2;
		for (int i = 0; i < fonts.length; i++) {
			if ((10+i) == font.getSize()) {
				selected = i;
			}
			fonts[i] = ""+(10 + i);
		}
		fontsize = new JComboBox(fonts);
		fontsize.setSelectedIndex(selected);
		add(fontsize);
		
		JLabel fontnamelabel = new JLabel("Font:");
		add(fontnamelabel);
		String[] fontnames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fontname = new JComboBox(fontnames);
		selected = -1;
		for (int i = 0; i < fontnames.length; i++) {
			if (selected == -1) {
				if ("Arial".compareTo(fontnames[i]) == 0) {
					selected = i;
				}
			}
			if (font.getFamily().compareTo(fontnames[i]) == 0) {
				selected = i;
			}
		}
		fontname.setSelectedIndex(selected);
		add(fontname);
		
		JLabel spacelabel = new JLabel("Space Indent Chorus:");
		add(spacelabel);
		String[] tabs = new String[8];
		selected = 0;
		for (int i = 0; i < 8; i++) {
			if ((i+1) == settings.getSpaceIndentChorus()) {
				selected = i;
			}
			tabs[i] = ""+(i+1);
		}
		spacecombo = new JComboBox(tabs);
		spacecombo.setSelectedIndex(selected);
		add(spacecombo);
		
		JLabel dummylabel = new JLabel("");
		add(dummylabel);
		doubleline = new JCheckBox("Double Line Space");
		doubleline.setSelected(settings.getDoubleLine());
		add(doubleline);
		
		JLabel dummylabel2 = new JLabel("");
		add(dummylabel2);
		usealtkey = new JCheckBox("Use Alternative Key");
		usealtkey.setSelected(settings.usealtkey);
		add(usealtkey);

		resetmykeys = new JButton("Reset");
		resetmykeys.addActionListener(this);
		add(resetmykeys);
		usemykeys = new JCheckBox("Use My Keys File");
		usemykeys.setSelected(settings.usemykeys);
		add(usemykeys);
		
		JLabel urllabel = new JLabel("SVN URL:");
		add(urllabel);
		svnurl = new JTextField();
			svnurl.setText(settings.svnurl);
		add(svnurl);
		JLabel usernamelabel = new JLabel("SVN Username:");
		add(usernamelabel);
		svnusername = new JTextField();
			svnusername.setText(settings.svnusername);
		add(svnusername);
		JLabel passwordlabel = new JLabel("SVN Password:");
		add(passwordlabel);
		svnpassword = new JPasswordField();
			svnpassword.setText(settings.svnpassword);
		add(svnpassword);
		
		JLabel gkeylabel = new JLabel("Spreadsheet Key:");
		add(gkeylabel);
		googlekey = new JTextField();
			googlekey.setText(settings.googlekey);
		add(googlekey);
		JLabel gsheetlabel = new JLabel("Worksheet Name:");
		add(gsheetlabel);
		googlesheet = new JTextField();
		googlesheet.setText(settings.googlesheet);
		add(googlesheet);
		JLabel gusernamelabel = new JLabel("Google Username:");
		add(gusernamelabel);
		googleusername = new JTextField();
			googleusername.setText(settings.googleusername);
		add(googleusername);
		JLabel gpasswordlabel = new JLabel("Google Password:");
		add(gpasswordlabel);
		googlepassword = new JPasswordField();
			googlepassword.setText(settings.googlepassword);
		add(googlepassword);

	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == folderbrowse) {
			File file = Main.OS().openFileDialog(this.getParentFrame(), true);
			if (file != null) {
				folder.setText(file.getPath());   
	        }
		} else if (e.getSource() == resetmykeys) {
			int n = JOptionPane.showConfirmDialog(
				this,"Are you sure you want to delete all your 'My Key' data?","Reset My Keys",JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				settings.resetMyKeys();
			}
		}
	}
	
	public void savePreferences() {
		settings.sourcefolder = folder.getText();
		settings.setDefaultFont((String) fontname.getSelectedItem(), Integer.parseInt((String) fontsize.getSelectedItem()));
		settings.setSpaceIndentChorus(Integer.parseInt((String) spacecombo.getSelectedItem()));
		settings.setDoubleLine(doubleline.isSelected());
		settings.svnusername = svnusername.getText();
		settings.svnurl = svnurl.getText();
		settings.svnpassword = svnpassword.getText();
		settings.googlekey = googlekey.getText();
		settings.googlesheet = googlesheet.getText();
		settings.googleusername = googleusername.getText();
		settings.googlepassword = googlepassword.getText();
		settings.usealtkey = usealtkey.isSelected();
		settings.usemykeys = usemykeys.isSelected();
		settings.saveSettings();
	}
}
