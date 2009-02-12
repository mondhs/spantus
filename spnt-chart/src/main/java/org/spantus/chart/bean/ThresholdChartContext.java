package org.spantus.chart.bean;

import net.quies.math.plot.ChartStyle;

import org.spantus.core.FrameValues;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 *
 */
public class ThresholdChartContext {

	FrameValues values;
	
	FrameValues threshold;
	
	FrameValues state;
	
	String description;
	
	ChartStyle style;
	
	Float order;

	public FrameValues getValues() {
		return values;
	}

	public void setValues(FrameValues values) {
		this.values = values;
	}

	public FrameValues getThreshold() {
		return threshold;
	}

	public void setThreshold(FrameValues threshold) {
		this.threshold = threshold;
	}

	public FrameValues getState() {
		return state;
	}

	public void setState(FrameValues state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ChartStyle getStyle() {
		return style;
	}

	public void setStyle(ChartStyle style) {
		this.style = style;
	}

	public Float getOrder() {
		return order;
	}

	public void setOrder(Float order) {
		this.order = order;
	}
}
