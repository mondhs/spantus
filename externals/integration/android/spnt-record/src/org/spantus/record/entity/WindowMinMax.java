package org.spantus.record.entity;

public class WindowMinMax {
	private Integer min = Integer.MAX_VALUE;
	private Integer max = -Integer.MAX_VALUE;
	public Integer getMin() {
		return min;
	}
	public void setMin(Integer min) {
		this.min = min;
	}
	public Integer getMax() {
		return max;
	}
	public void setMax(Integer max) {
		this.max = max;
	}
	@Override
	public String toString() {
		return "WindowMinMax [min=" + min + ", max=" + max + "]";
	}
	
	
}
