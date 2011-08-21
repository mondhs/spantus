package org.spantus.math;

import java.text.MessageFormat;

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
	@Override
	public String toString() {
		return MessageFormat.format("{0} [{1}->{2}]", getClass().getName(), getIndex(), getValue());
	}
}
