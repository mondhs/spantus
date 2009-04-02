package org.spantus.chart;

import java.math.BigDecimal;

import net.quies.math.plot.AxisInstance;
import net.quies.math.plot.XAxis;

public class XAxisGrid extends XAxis {

	public AxisInstance getInstance(BigDecimal min, BigDecimal max, int length) {
		return new XAxisGridInstance(this, min, max, length);
	}
}
