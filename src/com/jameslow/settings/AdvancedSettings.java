package com.jameslow.settings;

import java.util.*;
import com.jameslow.*;

public class AdvancedSettings extends Settings implements InternalChangeListener {
	public static final int OS_SPECIFIC = 0;
	public static final int FORCE_ONCONFIRM = 1;
	public static final int FORCE_IMMEDIATE = 2;
	
	private List changelisteners = new ArrayList();
	private List internallisteners = new ArrayList();
	private List settingstochange = new ArrayList();
	private int changemode;
	
	public AdvancedSettings() {
		this(OS_SPECIFIC);
	}
	public AdvancedSettings(int changemode) {
		this.changemode = changemode;
	}
	
	public void addSettingsChangeListener(SettingsChangeListener listener) {
		changelisteners.add(listener);
	}
	public void removeSettingsChangeListener(SettingsChangeListener listener) {
		changelisteners.remove(listener);
	}
	public void addInternalChangeListener(InternalChangeListener listener) {
		internallisteners.add(listener);
	}
	public void removeInternalChangeListener(InternalChangeListener listener) {
		internallisteners.remove(listener);
	}
	
	public boolean match(String setting) {
		return true;
	}
	public void settingChanged(String setting) {
		for (int i=0; i<internallisteners.size(); i++) {
			InternalChangeListener l = (InternalChangeListener) internallisteners.get(i);
			if (l.match(setting)) {
				l.settingChanged(setting);
			}
		}		
		if (changemode == FORCE_IMMEDIATE || (changemode == OS_SPECIFIC && Main.OS().settingsImmediate())) {
			for (int i=0; i<changelisteners.size(); i++) {
				SettingsChangeListener l = (SettingsChangeListener) changelisteners.get(i);
				if (l.match(setting)) {
					l.settingChanged(setting);
				}
			}
		} else {
			settingstochange.add(setting);
		}
	}
	public void confirmSettings() {
		if (changemode == FORCE_ONCONFIRM || (changemode == OS_SPECIFIC && !Main.OS().settingsImmediate())) {
			for (int j=0; j<settingstochange.size(); j++) {
				String setting = (String) settingstochange.get(j);
				for (int i=0; i<changelisteners.size(); i++) {
					SettingsChangeListener l = ((SettingsChangeListener) changelisteners.get(i));
					if (l.match(setting)) {
						l.settingChanged(setting);
					}
				}
			}
		}
		settingstochange.clear();
	}
	public void cancelSettings() {
		settingstochange.clear();
	}
}
