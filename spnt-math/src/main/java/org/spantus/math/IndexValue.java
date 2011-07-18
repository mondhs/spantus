package org.spantus.math;

public class IndexValue {
	private Integer index;
	private Double value;
	public IndexValue() {
	}
	
	public IndexValue(int index, Double value) {
		this.index = index;
		this.value = value;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
}
