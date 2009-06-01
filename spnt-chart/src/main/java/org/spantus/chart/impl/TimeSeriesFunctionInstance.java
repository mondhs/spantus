package org.spantus.chart.impl;

import java.math.BigDecimal;

import net.quies.math.plot.FunctionInstance;

import org.spantus.chart.ChartDescriptionInfo;
import org.spantus.chart.ChartDescriptionResolver;
import org.spantus.logger.Logger;

public abstract class TimeSeriesFunctionInstance implements FunctionInstance, ChartDescriptionResolver{

	protected Logger log = Logger.getLogger(this.getClass());

	String description;
	
	
	public ChartDescriptionInfo resolve(float time, float verticalPossition) {
		if(getCoordinateBoundary().getYMin().floatValue()<verticalPossition && 
				getCoordinateBoundary().getYMax().floatValue()>verticalPossition){
			String value = getValueOn(BigDecimal.valueOf(time));
			return new ChartDescriptionInfo(getDescription(), BigDecimal.valueOf(time), 
					value);
		}
		return null;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
