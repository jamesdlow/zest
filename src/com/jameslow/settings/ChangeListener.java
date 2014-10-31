package com.jameslow.settings;

public interface ChangeListener {
	public boolean match(String setting);
	public void settingChanged(String setting);
}
