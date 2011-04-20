package org.spantus.math;

public class IndexValue {
	private Integer index;
	private Float value;
	public IndexValue() {
	}
	
	public IndexValue(int index, float value) {
		this.index = index;
		this.value = value;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
}
