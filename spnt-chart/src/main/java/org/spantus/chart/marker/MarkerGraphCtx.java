package org.spantus.chart.marker;

import java.math.BigDecimal;

public class MarkerGraphCtx {
	BigDecimal xScalar;

	public BigDecimal getXScalar() {
		return xScalar;
	}

	public void setXScalar(BigDecimal scalar) {
		xScalar = scalar;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb
		.append(getClass().getSimpleName())
		.append(":[")
		.append(getXScalar())
		.append("]");
		return sb.toString();
	}

}
