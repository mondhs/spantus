package org.spantus.externals.recognition.bean;

import org.spantus.core.IValues;

public class FeatureData {
	String name;
	IValues values;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public IValues getValues() {
		return values;
	}
	public void setValues(IValues values) {
		this.values = values;
	}

}
