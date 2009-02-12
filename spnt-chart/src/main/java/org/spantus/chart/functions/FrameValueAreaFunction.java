package org.spantus.chart.functions;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Function;
import net.quies.math.plot.FunctionInstance;
import net.quies.math.plot.GraphDomain;

import org.spantus.chart.impl.AreaChartInstance;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

public class FrameValueAreaFunction extends Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4770519604910040851L;

	AreaChartInstance charType;

	public FrameValueAreaFunction(String description, FrameVectorValues vals,
			ChartStyle style) {
		super(description);
		charType = new AreaChartInstance(description, vals, style);
	}

	public void addFrameValues(FrameValues newvals) {
		throw new RuntimeException("Not impl");
	}

	
	public FunctionInstance getInstance(GraphDomain domain, ChartStyle style) {
		charType.setDomain(domain);
		return charType;
	}
	public void setOrder(float order) {
		charType.setOrder(order);
	}

	public AreaChartInstance getCharType() {
		return charType;
	}
}
