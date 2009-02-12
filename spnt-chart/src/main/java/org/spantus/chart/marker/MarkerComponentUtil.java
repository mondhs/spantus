package org.spantus.chart.marker;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class MarkerComponentUtil {
	
	public static int timeToScreen(MarkerGraphCtx ctx, BigDecimal val){
		BigDecimal startX = val.divide(ctx.getXScalar(), RoundingMode.HALF_UP);
		return startX.intValue()/1000;
	}
	
	public static BigDecimal screenToTime(MarkerGraphCtx ctx, int val){
		BigDecimal start = BigDecimal.valueOf(val*1000).multiply(ctx.getXScalar());
		return start;
	}

}
