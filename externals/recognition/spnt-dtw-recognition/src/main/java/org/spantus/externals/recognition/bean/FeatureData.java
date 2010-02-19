package org.spantus.externals.recognition.bean;

import org.spantus.core.FrameVectorValues;

public class FeatureData {
	String name;
	FrameVectorValues values;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FrameVectorValues getValues() {
		return values;
	}
	public void setValues(FrameVectorValues values) {
		this.values = values;
	}

}
