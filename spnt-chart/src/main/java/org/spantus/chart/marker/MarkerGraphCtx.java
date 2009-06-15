package org.spantus.chart.marker;

import java.math.BigDecimal;

public class MarkerGraphCtx {
	BigDecimal xScalar;
	BigDecimal xOffset;

	public BigDecimal getXScalar() {
		return xScalar;
	}

	public void setXScalar(BigDecimal scalar) {
		xScalar = scalar;
	}
	
	public BigDecimal getXOffset() {
		return xOffset;
	}

	public void setXOffset(BigDecimal offset) {
		xOffset = offset;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb
		.append(getClass().getSimpleName())
		.append(":[")
		.append(getXScalar())
		.append("; offset")
		.append(getXOffset())
		.append("]");
		return sb.toString();
	}

	

}
