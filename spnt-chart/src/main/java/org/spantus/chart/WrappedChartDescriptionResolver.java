/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.chart;

import org.spantus.chart.impl.TimeSeriesMultiChart;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 * Created on Feb 22, 2009
 */
public class WrappedChartDescriptionResolver implements
		ChartDescriptionResolver {
	
	ChartDescriptionResolver localResolver;
	
	Logger log = Logger.getLogger(WrappedChartDescriptionResolver.class);
	
	public WrappedChartDescriptionResolver() {
	}
	
	public WrappedChartDescriptionResolver(ChartDescriptionResolver localResolver) {
		this.localResolver = localResolver;
	}
	
	public ChartDescriptionInfo resolve(Long time, float value) {
		ChartDescriptionInfo resolved = localResolver.resolve(time,value);
		if(resolved == null) return null;
		String resolvedStr = resolved.getName();
		resolvedStr = resolvedStr.replaceAll("BUFFERED_", "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.AREA_CHART_PREFIX, "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.MATRIX_CHART_PREFIX, "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.CHART_PREFIX, "");
		resolvedStr = resolvedStr.replaceAll(TimeSeriesMultiChart.THRESHOLD_PREFIX, "");
		resolved.setName(resolvedStr);
//		log.debug("resolved: " + resolved );	
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
