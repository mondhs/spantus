package org.spantus.chart;

import java.math.BigDecimal;

public class ChartDescriptionInfo {
	private String name;
	private BigDecimal value;
	
	public ChartDescriptionInfo() {
	}
	
	public ChartDescriptionInfo(String name,	BigDecimal value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getName() + "=" + getValue();
	}
}
