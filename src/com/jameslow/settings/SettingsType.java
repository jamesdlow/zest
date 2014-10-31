package com.jameslow.settings;

import javax.swing.JPanel;

public abstract class SettingsType extends JPanel {
	protected InternalChangeListener listener;
	
	public abstract String getRootName();
	public abstract String getTitle();
	
	public void setInternalChangeListener(InternalChangeListener listener) {
		this.listener = listener;
	}
}