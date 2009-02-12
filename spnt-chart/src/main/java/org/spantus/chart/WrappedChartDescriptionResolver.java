package org.spantus.chart;

import org.spantus.chart.impl.TimeSeriesMultiChart;
import org.spantus.logger.Logger;

public class WrappedChartDescriptionResolver implements
		ChartDescriptionResolver {
	
	ChartDescriptionResolver localResolver;
	
	Logger log = Logger.getLogger(getClass());
	
	public WrappedChartDescriptionResolver() {
	}
	
	public WrappedChartDescriptionResolver(ChartDescriptionResolver localResolver) {
		this.localResolver = localResolver;
	}
	
	public ChartDescriptionInfo resolve(float val) {
		ChartDescriptionInfo resolved = localResolver.resolve(val);
		if(resolved == null) return null;
		String resolvedStr = resolved.getName();
		resolvedStr = resolvedStr.replaceAll("BUFFERED_", "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.AREA_CHART_PREFIX, "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.CHART_PREFIX, "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.MATRIX_CHART_PREFIX, "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.THRESHOLD_PREFIX, "");
		resolved.setName(resolvedStr);
		log.debug("resolved: " + resolved );	
		return resolved;
	}
	
	public ChartDescriptionResolver getInstance(ChartDescriptionResolver localResolver){
		return new WrappedChartDescriptionResolver(localResolver);
	}
	protected ChartDescriptionResolver getLocalResolver() {
		return localResolver;
	}
	protected void setLocalResolver(ChartDescriptionResolver localResolver) {
		this.localResolver = localResolver;
	}


}
