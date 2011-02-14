package org.spantus.exp.recognition;

import java.text.MessageFormat;

public class LabelStatistics {
	String label;
	Long length;
	int count;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public static String getHeader(){
		return "Label;Length;Count";
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0};{1,number,#};{2}", getLabel(),getLength(), getCount());
	}
}
