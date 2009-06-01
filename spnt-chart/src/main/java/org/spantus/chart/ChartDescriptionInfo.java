package org.spantus.chart;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class ChartDescriptionInfo {
	private String name;
	private String value;
	private BigDecimal time;
	
	public ChartDescriptionInfo() {
	}
	
	public ChartDescriptionInfo(String name, BigDecimal time, String value) {
		this.name = name;
		this.value = value;
		this.time = time;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public BigDecimal getTime() {
		return time;
	}

	public void setTime(BigDecimal time) {
		this.time = time;
	}
	
	
	@Override
	public String toString() {
		String message = MessageFormat.format("{0}:{1}[{2}]={3} ", getClass().getSimpleName(),
				getName(),
				getTime(),
				getValue()
				);
		return  message;
	}

	
}
