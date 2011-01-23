package org.spantus.chart;

import java.math.BigDecimal;

import net.quies.math.plot.AxisInstance;
import net.quies.math.plot.XAxis;
import net.quies.math.plot.XAxisInstance;

public class XAxisGrid extends XAxis {
	
	boolean gridOn;
	
	public XAxisGrid() {
	}
	
	
	public AxisInstance getInstance(BigDecimal min, BigDecimal max, int length) {
		if(isGridOn()){
			return new XAxisGridInstance(this, min, max, length); 
		}
		return new XAxisInstance(this, min, max, length);
	}



	public boolean isGridOn() {
		return gridOn;
	}


	public void setGridOn(boolean gridOn) {
		this.gridOn = gridOn;
	}
}
