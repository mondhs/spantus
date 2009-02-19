package org.spantus.chart.marker;

import java.math.BigDecimal;

public abstract class MarkerComponentUtil {
	
	public static int timeToScreen(MarkerGraphCtx ctx, Long val){
		Long startX = val/ctx.getXScalar().longValue();
		return startX.intValue()/1000;
	}
	
	public static Long screenToTime(MarkerGraphCtx ctx, int val){
		BigDecimal start = BigDecimal.valueOf(val*1000).multiply(ctx.getXScalar());
		return start.longValue();
	}

}
