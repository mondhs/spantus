package org.spantus.chart.impl;

import java.math.BigDecimal;

import org.spantus.chart.ChartDescriptionInfo;
import org.spantus.chart.ChartDescriptionResolver;
import org.spantus.logger.Logger;

import net.quies.math.plot.FunctionInstance;

public abstract class TimeSeriesFunctionInstance implements FunctionInstance, ChartDescriptionResolver{

	protected Logger log = Logger.getLogger(this.getClass());

	String description;
	
	
	public ChartDescriptionInfo resolve(float val) {
		if(getCoordinateBoundary().getYMin().floatValue()<val && 
				getCoordinateBoundary().getYMax().floatValue()>val){
			return new ChartDescriptionInfo(getDescription(), BigDecimal.valueOf(val));
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
