package com.jameslow.zest;

import java.awt.Font;
import java.io.*;
import java.util.*;
import com.jameslow.*;
import com.jameslow.settings.*;
import com.jameslow.zest.util.*;
import com.jameslow.pulp.*;

public class ZestSettings extends AdvancedSettings {
	public static final String FOLDER = "Folder";
	public static final String USE_ALTKEY = "UseAltKey";
	public static final String USE_MYKEYS = "UseMyKeys";
	public static final String SVN = "SVN";
	public static final String SVN_URL = SVN+"."+"Url";
	public static final String SVN_USERNAME = SVN+"."+"Username";
	public static final String SVN_PASSWORD = SVN+"."+"Password";
	public static final String GOOGLE = "Google";
	public static final String GOOGLE_KEY = GOOGLE+"."+"Key";
	public static final String GOOGLE_SHEET = GOOGLE+"."+"Sheet";
	public static final String GOOGLE_USERNAME = GOOGLE+"."+"Username";
	public static final String GOOGLE_PASSWORD = GOOGLE+"."+"Password";
	public static final String STYLES = "Styles";
		public static final String STYLE = STYLES + "." + "Style";
			public static final String STYLE_NAME = "Name";
				public static final String STYLE_NAME_DEFAULT = "Default"; 
			public static final String FORMATS = "Formats";
				public static final String FORMAT = "Format" + "." + "Format";
					//public static final String FORMAT_NAME = "Name";
						//Verse,Chorus,Unknown etc.
					public static final String FORMAT_DISPLAY = "Display";
					public static final String FORMAT_SPACEINDENT = "SpaceIndent";
					public static final String FORMAT_TABINDENT = "TabIndent";
					public static final String FORMAT_DISPLAYBLANK = "DisplayBlank";
					public static final String FORMAT_DISPLAYCOLON = "DisplayColon";
					public static final String FORMAT_DISPLAYSPACE = "DisplaySpace";
					public static final String FORMAT_DISPLAYCOUNT = "DisplayCount";
					public static final String FORMAT_DOUBLELINE = "DoubleLine";
					public static final String FORMAT_DISPLAYFIRSTCHORDSONLY = "DisplayFirstChordsOnly";
					public static final String FORMAT_PROPERTIESSAMELINE = "PropertiesSaneLine";
		
	public static final String FONTS = "Fonts";
	public static final String FONT = FONTS + "." + "Font";
		//public static final String FONT_NAME = FONT + "." + "Name";
			public static final String TITLEFONT = "TitleFont";
			public static final String WORDFONT = "WordFont";
			public static final String CHORDFONT = "ChordFont";
			public static final String WHITESPACEFONT = "WhiteSpaceFont";
		public static final String FONT_FAMILY = FONT + "." + "Face";
		public static final String FONT_STYLE = FONT + "." + "Style";
		public static final String FONT_SIZE = FONT + "." + "Size";
		public static final String FONT_ALIGN = FONT + "." + "Align";
		public static final String FONT_COLOR = FONT + "." + "Color";
		public static final String FONT_DISPLAY = FONT + "." + "Display";
		
	public static final String MYKEYS = Main.OS().settingsDir() + Main.OS().fileSeparator() + "MyKeys.properties";
	public static final String INITIAL_SONG = "Amazing Grace.txt";
	
	protected Properties mykeys = new Properties();
	protected PageSizes pagesizes;
	protected String sourcefolder;
	protected LinkedHashMap<String,Map<SectionType,Format>> styles = new LinkedHashMap<String,Map<SectionType,Format>>();
	protected String svnusername,svnpassword,svnurl;
	protected String googleusername,googlepassword,googlekey,googlesheet;
	protected boolean usealtkey;
	protected boolean usemykeys;
	
	public void loadSettings() {
		pagesizes = new PageSizes(this);
		sourcefolder = getSetting(FOLDER,"");
		usealtkey = getSetting(USE_ALTKEY,false);
		usemykeys = getSetting(USE_MYKEYS,true);
		svnusername = getSetting(SVN_USERNAME,"");
		String temppassword = getSetting64(SVN_PASSWORD,"");
		if (temppassword.length() > 2*getTitle().length()) {
			svnpassword = temppassword.substring(getTitle().length(), temppassword.length() - getTitle().length());
		} else {
			svnpassword = "";
		}
		svnurl = getSetting(SVN_URL,"");
		
		googlekey = getSetting(GOOGLE_KEY,"");
		googlesheet = getSetting(GOOGLE_SHEET,"");
		googleusername = getSetting(GOOGLE_USERNAME,"");
		googlepassword = getSetting64(GOOGLE_PASSWORD,"");
		
		File folder = new File(sourcefolder);
		if ("".compareTo(sourcefolder) == 0 || !folder.exists() || !folder.isDirectory() || !folder.canRead()) {
			try {
				sourcefolder = Main.OS().settingsDir() + Main.OS().fileSeparator() + "Songs";
				(new File(sourcefolder)).mkdirs();
				if (FileUtils.IsEmpty(sourcefolder)) {
					FileUtils.WriteStream(getResourceAsStream(INITIAL_SONG), sourcefolder + Main.OS().fileSeparator() + INITIAL_SONG);
				}
			} catch (Exception e) {
				Main.Logger().severe(e.getMessage());
			}
		}
		LoadStyles();
		try {
			mykeys.load(new FileInputStream(MYKEYS));
		} catch (IOException e) {
			Main.Logger().info("Could not load my keys file "+MYKEYS);
		}
	}
	public void preSaveSettings() {
		setSetting(SVN_USERNAME, svnusername);
		setSetting(SVN_URL, svnurl);
		setSetting64(SVN_PASSWORD, getTitle() + svnpassword + getTitle());
		setSetting(GOOGLE_KEY, googlekey);
		setSetting(GOOGLE_SHEET, googlesheet);
		setSetting(GOOGLE_USERNAME, googleusername);
		setSetting64(GOOGLE_PASSWORD, googlepassword);
		setSetting(FOLDER, sourcefolder);
		setSetting(USE_ALTKEY,usealtkey);
		setSetting(USE_MYKEYS,usemykeys);
		SaveStyles();
	}
	
	public Map<SectionType,Format> getCurrentStyle() {
		if (styles.size() == 0) {
			setDefaultStyle();
		}
		return styles.get(styles.keySet().toArray()[0]);
	}
	
	//Temporary setting stuff, to make things simple for now
	public void setDefaultStyle() {
		Map<SectionType,Format> style = new HashMap<SectionType,Format>();
		FontFormat alltitlefontformat = new FontFormat();
			alltitlefontformat.display = false;
		Format propformat = new Format();
			propformat.display = false;
			
		for(SectionType type : SectionType.values()) {
			if (type == SectionType.TITLE) {
				Format titleformat = new Format();
				titleformat.setTitleFont(alltitlefontformat);	
				Font titlefont = new Font("Arial",Font.BOLD,12);
				FontFormat titlefontformat = new FontFormat();
				titlefontformat.align = FontFormat.Align.CENTER;
				titlefontformat.font = titlefont;
				titleformat.setWordFont(titlefontformat);
				style.put(SectionType.TITLE,titleformat);
			} else if (type.isInfoTag()) {
				style.put(type,propformat);
			}
		}
			
		Format verseformat = new Format();
			verseformat.setTitleFont(alltitlefontformat);
			style.put(SectionType.VERSE,verseformat);
		
		Format chorusformat = new Format();
			chorusformat.setTitleFont(alltitlefontformat);
			chorusformat.spaceindent = 4;
			Font wordfont = new Font("Arial",Font.ITALIC,12);
			FontFormat chorusfontformat = new FontFormat();
			chorusfontformat.font = wordfont;
			chorusformat.setWordFont(chorusfontformat);
			style.put(SectionType.CHORUS,chorusformat);
		
		Format keyformat = new Format();
			keyformat.display = false;
			style.put(SectionType.KEY, keyformat);
		styles.put(STYLE_NAME_DEFAULT, style);
	}
	public void setDefaultFont(String font, int size) {
		for(Map.Entry<SectionType, Format> e : getCurrentStyle().entrySet()) {
			e.getValue().getWordFont().font = new Font(font,e.getValue().getWordFont().font.getStyle(),size);
		}
	}
	public void setDoubleLine(boolean doubleline) {
		for(Map.Entry<SectionType, Format> e : getCurrentStyle().entrySet()) {
			e.getValue().doubleline = doubleline;
		}
	}
	public void setSpaceIndentChorus(int spaceindent) {
		for(Map.Entry<SectionType, Format> e : getCurrentStyle().entrySet()) {
			if (e.getKey() == SectionType.CHORUS) {
				e.getValue().spaceindent = spaceindent;
			}
		}
	}
	public Font getDefaultFont() {
		for(Map.Entry<SectionType, Format> e : getCurrentStyle().entrySet()) {
			return e.getValue().getWordFont().font;
		}
		return new Font("Arial",Font.PLAIN,12);
	}
	public boolean getDoubleLine() {
		for(Map.Entry<SectionType, Format> e : getCurrentStyle().entrySet()) {
			return e.getValue().doubleline;
		}
		return false;
	}
	public int getSpaceIndentChorus() {
		for(Map.Entry<SectionType, Format> e : getCurrentStyle().entrySet()) {
			if (e.getKey() == SectionType.CHORUS) {
				return e.getValue().spaceindent;
			}
		}
		return 0;
	}

	//Zest specific stuff
	private void SaveStyles() {
		getXMLHelper().deleteSubNode(STYLES);
		for(Map.Entry<String, Map<SectionType, Format>> e : styles.entrySet()) {
			SaveStyle(e.getValue(), e.getKey());
		}
	}
	private void SaveStyle(Map<SectionType,Format> style, String stylename) {
		XMLHelper stylesetting = getXMLHelperByName(STYLE, stylename);
		for(Map.Entry<SectionType, Format> e : style.entrySet()) {
			SaveFormat(stylesetting, e.getValue(), e.getKey());
		}
	}
	private void SaveFormat(XMLHelper stylesetting, Format format, SectionType sectiontype) {
		XMLHelper formatsetting = stylesetting.getSubNodeByName(FORMAT, sectiontype.name());
		SaveFont(formatsetting,format.getWordFont(),WORDFONT);
		if (format.getWordFont() != format.getTitleFont()) {
			SaveFont(formatsetting,format.getTitleFont(),TITLEFONT);
		}
		if (format.getWordFont() != format.getChordFont()) {
			SaveFont(formatsetting,format.getChordFont(),CHORDFONT);
		}
		if (format.getWordFont() != format.getWhiteSpaceFont()) {
			SaveFont(formatsetting,format.getWhiteSpaceFont(),WHITESPACEFONT);
		}
		formatsetting.setValue(FORMAT_DISPLAY,format.display);
		formatsetting.setValue(FORMAT_DISPLAYCOLON,format.displaytitlecolon);
		formatsetting.setValue(FORMAT_DISPLAYSPACE,format.displaytitlespace);
		formatsetting.setValue(FORMAT_DISPLAYBLANK,format.displayblank);
		formatsetting.setValue(FORMAT_DISPLAYCOUNT,format.displaycount);
		formatsetting.setValue(FORMAT_SPACEINDENT,format.spaceindent);
		formatsetting.setValue(FORMAT_TABINDENT,format.tabindent);
		formatsetting.setValue(FORMAT_DOUBLELINE,format.doubleline);
		formatsetting.setValue(FORMAT_DISPLAYFIRSTCHORDSONLY,format.displayfirstchordsonly);
		formatsetting.setValue(FORMAT_PROPERTIESSAMELINE,format.propertiessameline);
	}
	private void SaveFont(XMLHelper formatsetting, FontFormat format, String name) {
		XMLHelper fontsetting = formatsetting.getSubNodeByName(FONT, name);
		fontsetting.setValue(FONT_FAMILY,format.font.getFamily());
		fontsetting.setValue(FONT_STYLE,format.font.getStyle());
		fontsetting.setValue(FONT_SIZE,format.font.getSize());
		fontsetting.setValue(FONT_ALIGN,format.align.ordinal());
		fontsetting.setValue(FONT_COLOR,format.color);
		fontsetting.setValue(FONT_DISPLAY,format.display);
	}
	private void LoadStyles() {
		XMLHelper[] stylesettings = getXMLHelpers(STYLE);
		if (stylesettings.length > 0) {
			for (XMLHelper stylexml : stylesettings) {
				String name = stylexml.getNameValue();
				styles.put(name, LoadStyle(stylexml));
			}
		} else {
			getCurrentStyle();
		}
	}
	private Map<SectionType,Format> LoadStyle(XMLHelper stylexml) {
		Map<SectionType,Format> style = new HashMap<SectionType,Format>();
		XMLHelper[] formatsettings = stylexml.getSubNodeList(FORMAT);
		if (formatsettings.length > 0) {
			for (XMLHelper formatxml : formatsettings) {
				String name = formatxml.getNameValue();
				SectionType section = SectionType.parse(name);
				style.put(section, LoadFormat(formatxml));
			}
		}
		return style;
	}
	private Format LoadFormat(XMLHelper formatxml) {
		Format format = new Format();
		format.display = formatxml.getValue(FORMAT_DISPLAY,format.display);
		format.spaceindent = formatxml.getValue(FORMAT_SPACEINDENT,format.spaceindent);
		format.tabindent = formatxml.getValue(FORMAT_TABINDENT,format.tabindent);
		format.displayblank = formatxml.getValue(FORMAT_DISPLAYBLANK,format.displayblank);
		format.displaytitlecolon = formatxml.getValue(FORMAT_DISPLAYCOLON,format.displaytitlecolon);
		format.displaytitlespace = formatxml.getValue(FORMAT_DISPLAYSPACE,format.displaytitlespace);
		format.displaycount = formatxml.getValue(FORMAT_DISPLAYCOUNT,format.displaycount);
		format.doubleline = formatxml.getValue(FORMAT_DOUBLELINE,format.doubleline);
		format.displayfirstchordsonly = formatxml.getValue(FORMAT_DISPLAYFIRSTCHORDSONLY,format.displayfirstchordsonly);
		format.propertiessameline = formatxml.getValue(FORMAT_PROPERTIESSAMELINE,format.propertiessameline);
		
		XMLHelper[] fontsettings = formatxml.getSubNodeList(FONT); 
		if (fontsettings.length > 0) {
			for (XMLHelper fontxml : fontsettings) {
				String name = fontxml.getNameValue();
				FontFormat font = LoadFont(fontxml);
				if (WORDFONT.compareTo(name) == 0) {
					format.setWordFont(font);
				} else if (TITLEFONT.compareTo(name) == 0) {
					format.setTitleFont(font);
				} else if (CHORDFONT.compareTo(name) == 0) {
					format.setChordFont(font);
				} else if (CHORDFONT.compareTo(name) == 0) {
					format.setWhiteSpaceFont(font); 
				}
			}
		}
		return format;
	}
	private FontFormat LoadFont(XMLHelper fontxml) {
		FontFormat font = new FontFormat();
		String fontfamily = fontxml.getValue(FONT_FAMILY, font.font.getFamily());
		int fontstyle = fontxml.getValue(FONT_STYLE, font.font.getStyle());
		int fontsize = fontxml.getValue(FONT_SIZE, font.font.getSize());
		font.font = new Font(fontfamily, fontstyle, fontsize);
		int fontalign = fontxml.getValue(FONT_ALIGN,0);
		
		if (fontalign == FontFormat.Align.JUSTIFIED.ordinal()) {
			font.align = FontFormat.Align.JUSTIFIED;
		} else if (fontalign == FontFormat.Align.CENTER.ordinal()) {
			font.align = FontFormat.Align.CENTER;
		} else if (fontalign == FontFormat.Align.RIGHT.ordinal()) {
			font.align = FontFormat.Align.RIGHT;
		} else if (fontalign == FontFormat.Align.LEFT.ordinal()) {
			font.align = FontFormat.Align.LEFT;
		}
		font.color = fontxml.getValue(FONT_COLOR,font.color);
		font.display = fontxml.getValue(FONT_DISPLAY,font.display);
		return font;
	}
	
	public void postSaveSettings() {
		if (Main.Window() != null) {
			((ZestWindow) Main.Window()).setFolder();
		}
	}
	public String getMyKey(String filename, String currentkey) {
		return mykeys.getProperty(FileUtils.getFilename(filename), currentkey);
	}
	public void setMyKey(String filename, String mykey) {
		mykeys.put(FileUtils.getFilename(filename), mykey);
		saveMyKeys();
	}
	private void saveMyKeys() {
		try {
			mykeys.store(new FileOutputStream(MYKEYS), null);
		} catch (IOException e) {
			Main.Logger().warning("Could not save my keys file.");
		}
	}
	public void resetMyKeys() {
		mykeys = new Properties();
		saveMyKeys();
	}
}
