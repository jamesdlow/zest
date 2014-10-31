package com.jameslow.settings;

import java.util.*;

public abstract class SettingsMultiType extends SettingsType implements InternalChangeListener {
	private List subsettings = new ArrayList();
	
	public SettingsMultiType() {
		setLayout(null);
	}
	public List getSubNames() {
		List subnames = new ArrayList();
		subnames.add(getRootName());
		for(int i=0; i<subsettings.size(); i++) {
			SettingsType sub = (SettingsType) subnames.get(i);
			subnames.add(sub.getRootName());
			if (sub instanceof SettingsMultiType) {
				subnames.addAll(((SettingsMultiType) sub).getSubNames());
			}
		}
		return subnames;
	}
	
	public void addSubSettingsType(SettingsType type, int x, int y) {
		subsettings.add(type);
		add(type);
		type.setInternalChangeListener(this);
	}
	public boolean match(String setting) {
		return true;
	}
	public void settingChanged(String setting) {
		if (listener != null) {
			listener.settingChanged(setting);
		}
	}
}