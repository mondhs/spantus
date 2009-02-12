/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.chart.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Graph;
import net.quies.math.plot.GraphDomain;
import net.quies.math.plot.ZoomListener;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartDescriptionResolver;
import org.spantus.chart.InteractiveChart;
import org.spantus.chart.SignalSelectionListener;
import org.spantus.chart.SpantusChartToolbar;
import org.spantus.chart.WrappedChartDescriptionResolver;
import org.spantus.chart.bean.ChartInfo;
import org.spantus.chart.bean.ThresholdChartContext;
import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.chart.functions.FrameValueAreaFunction;
import org.spantus.chart.functions.FrameValueFuncton;
import org.spantus.chart.functions.FrameValueMatrixFuncton;
import org.spantus.chart.functions.FrameValueThearsholdFuncton;
import org.spantus.chart.service.ColorResolver;
import org.spantus.chart.service.IColorResolver;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.threshold.IThreshold;
import org.spantus.logger.Logger;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created 2008.04.11
 * 
 */
public class TimeSeriesMultiChart extends AbstractSwingChart {
	Logger log = Logger.getLogger(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -2779476925528044951L;

	private InteractiveChart graph = null;

	WrappedChartDescriptionResolver globalMessageResolver;
	private Set<SignalSelectionListener> listeners;
	private SpantusChartToolbar toolbar = null;
	private IColorResolver colorResolver;


	public static int VIEW_MATRIX_MIN_SIZE = 5;
	public static int VIEW_AREA_SIZE = 2;
	public static String THRESHOLD_PREFIX = "_THRESHOLD";
	public static String CHART_PREFIX = "_CHART";
	public static String  MATRIX_CHART_PREFIX = "_MATRIX_CHART";
	public static String  AREA_CHART_PREFIX = "_AREA_CHART";
	/**
	 * 
	 * @param reader
	 */
	public TimeSeriesMultiChart(IExtractorInputReader reader) {
		this(reader, null);
	}

	public TimeSeriesMultiChart(IExtractorInputReader reader, WrappedChartDescriptionResolver globalMessageResolver) {
		this.globalMessageResolver = globalMessageResolver;
		InteractiveChart interactiveChart = new InteractiveChart();
		interactiveChart.setSize(getSize());
		interactiveChart.addZoomListeners(new WrapedZoomlistener());
		// graph.getXAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		// graph.getYAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		setBackground(Color.WHITE);
		interactiveChart.setBackground(Color.WHITE);
		int i = 0;
		for (IExtractor buff : reader.getExtractorRegister()) {
			ChartDescriptionResolver resolver = null;
			if(buff instanceof IThreshold){
				resolver = addFunction(interactiveChart, (IThreshold)buff, i++);
			}else{
				resolver = addFunction(interactiveChart, buff, i++);
			}

			if(resolver == null){
				i--;
			}else{
				interactiveChart.addResolver(getGlobalMessageResolver().getInstance(resolver));	
			}
		}
		for (IExtractorVector buff : reader.getExtractorRegister3D()) {
			ChartDescriptionResolver resolver = addFunction(interactiveChart, buff, i++);
			interactiveChart.addResolver(getGlobalMessageResolver().getInstance(resolver));
		}
		add(interactiveChart, BorderLayout.CENTER);
		graph = interactiveChart;
		if(interactiveChart.getToolBar() instanceof SpantusChartToolbar){
			toolbar = (SpantusChartToolbar)interactiveChart.getToolBar(); 
			listeners = toolbar.getSignalSelectionListeners();
		}
	}
	/**
	 * 
	 * @param graph
	 * @param name
	 * @param vals
	 * @param order
	 */
	private TimeSeriesFunctionInstance addFunction(Graph graphChart, IExtractor extr,
			int order) {
		ChartStyle style1 = new ChartStyle();
		style1.setPaint(getColorResolver().resolveColor(extr));
		FrameValueFuncton function = new FrameValueFuncton(extr.getName() + CHART_PREFIX,
				extr.getOutputValues(), style1);
		function.setOrder(order);
		if(extr.getOutputValues().size() > 1){
			graphChart.addFunction(function, style1);	
		}else{
			log.debug("values is size: " + extr.getOutputValues().size() +"; " + function.getCharType().getDescription()
					+ " still will be added");
//			return null;
		}
		return function.getCharType();
	}
	private TimeSeriesFunctionInstance addFunction(Graph graphChart, IThreshold extr,
			int order) {
		ChartStyle style1 = new ChartStyle();
		style1.setPaint(getColorResolver().resolveColor(extr));
		
		ThresholdChartContext ctx = new ThresholdChartContext();
		ctx.setDescription(extr.getName() + THRESHOLD_PREFIX );
		ctx.setValues(extr.getOutputValues());
		ctx.setThreshold(extr.getThresholdValues());
		ctx.setStyle(style1);
		ctx.setState(extr.getState());
		
		FrameValueThearsholdFuncton function = new FrameValueThearsholdFuncton(ctx);
		function.setOrder(order);
		if(extr.getOutputValues().size() > 1){
			graphChart.addFunction(function, style1);	
		}else{
			log.debug("values is size: " + extr.getOutputValues().size() +"; " + function.getCharType().getDescription()
					+ " still will be added");
//			return null;
		}
		return function.getCharType();
	}
	
	/**
	 * 
	 * @param graph
	 * @param name
	 * @param vals
	 * @param order
	 */
	private TimeSeriesFunctionInstance addAreaFunction(Graph graphChart, IExtractorVector extr,
			int order) {
		ChartStyle style1 = new ChartStyle();
		style1.setPaint(getColorResolver().resolveColor(extr));
		FrameValueAreaFunction function = new FrameValueAreaFunction(extr.getName()
				+ AREA_CHART_PREFIX, extr.getOutputValues(), style1);
		function.setOrder(order);
		graphChart.addFunction(function, style1);
		return function.getCharType();
	}
	/**
	 * 
	 * @param graph
	 * @param name
	 * @param vals
	 * @param order
	 */
	private TimeSeriesFunctionInstance addFunction(Graph graphChart, IExtractorVector extr,
			int order) {
		TimeSeriesFunctionInstance charType = null;
		if (extr.getOutputValues().getFirst().size() == VIEW_AREA_SIZE) {
			charType = addAreaFunction(graphChart, extr, order);
//		} else if (vals.getFirst().size() < VIEW_MATRIX_MIN_SIZE) {
//			for (FrameValues fv : vals.transform()) {
//				addFunction(graph, name, fv, order);
//			}
		} else {
			FrameValueMatrixFuncton function = new FrameValueMatrixFuncton(extr.getName()
					+ MATRIX_CHART_PREFIX , extr.getOutputValues());
			ChartStyle style1 = new ChartStyle();
			style1.setPaint(getColorResolver().resolveColor(extr));
			function.setOrder(order);
			charType = function.getCharType();
			function.getCharType().setColorType(getColorType(order));
			graphChart.addFunction(function, style1);
		}
		return charType;
	}

	private VectorSeriesColorEnum getColorType(int i){
		return VectorSeriesColorEnum.blackWhite;
	}

	
	public void repaint() {
		super.repaint();
		if (graph != null) {
			graph.setSize(getSize());
			graph.render();
			graph.repaint();
		}
	}

	/**
	 * if toolbar is instanceof SpantusToolbar, listeners will be stored in SpantusToolbar 
	 * 
	 * @return
	 */
	public Set<SignalSelectionListener> getSignalSelectionListeners() {
		if (listeners == null) {
			listeners = new HashSet<SignalSelectionListener>();
		}
		return listeners;
	}

	/**
	 * 
	 * @param listener
	 */
	
	public void addSignalSelectionListener(SignalSelectionListener listener) {
		getSignalSelectionListeners().add(listener);
	}

	/**
	 * 
	 * @param from
	 * @param length
	 */
	public void notifySignalSelectionListeners(float from, float length) {
		for (SignalSelectionListener listener : getSignalSelectionListeners()) {
			listener.selectionChanged(from, length);
		}
	}

	private class WrapedZoomlistener implements ZoomListener {
		
		public void zoomChanged(GraphDomain domain) {
			if (domain == null || domain.getUntil() == null
					|| domain.getFrom() == null) {
				notifySignalSelectionListeners(0, 0);
				return;
			}
			float length = domain.getUntil().floatValue()
					- domain.getFrom().floatValue();
			notifySignalSelectionListeners(domain.getFrom().floatValue(),
					length);
		}
	}

	
	public ChartInfo getCharInfo() {
		if(toolbar != null){
			return toolbar.getChartInfo();
		}
		return super.getCharInfo();
	}
	
	public void setCharInfo(ChartInfo info) {
		if(toolbar != null){
			toolbar.setCharInfo(info);
			return;
		}
		super.setCharInfo(info);
	}
	public IColorResolver getColorResolver() {
		if(colorResolver == null){
			colorResolver = new ColorResolver();
		}
		return colorResolver;
	}
	public void setColorResolver(IColorResolver colorResolver) {
		this.colorResolver = colorResolver;
	}
	
	protected WrappedChartDescriptionResolver getGlobalMessageResolver() {
		if(globalMessageResolver == null){
			globalMessageResolver = new WrappedChartDescriptionResolver();
		}
		return globalMessageResolver;
	}
	protected void setGlobalMessageResolver(
			WrappedChartDescriptionResolver globalMessageResolver) {
		this.globalMessageResolver = globalMessageResolver;
	}

	@Override
	public void changedZoom(float from, float length) {
		GraphDomain domain = new GraphDomain(from, from+length);
		graph.setDomain(domain);
		graph.setZoomSelection(null);
		graph.render();
		graph.repaint();
	}

	public InteractiveChart getGraph() {
		return graph;
	}
	
}
