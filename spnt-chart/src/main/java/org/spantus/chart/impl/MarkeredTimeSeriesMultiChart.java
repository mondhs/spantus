package org.spantus.chart.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.math.RoundingMode;

import net.quies.math.plot.AxisInstance;

import org.spantus.chart.WrappedChartDescriptionResolver;
import org.spantus.chart.marker.MarkerGraph;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;

public class MarkeredTimeSeriesMultiChart extends TimeSeriesMultiChart {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MarkerGraph markerGraph;
	MouseListener markerGrpahMouseListener;


	public MarkeredTimeSeriesMultiChart(IExtractorInputReader reader,
			WrappedChartDescriptionResolver globalMessageResolver) {
		super(reader, globalMessageResolver);
	}
	
	public void initialize(MarkerSetHolder holder, MouseListener mouseListener){
		add(getMarkerGraph(), BorderLayout.NORTH);
		AxisInstance axisX = getGraph().getXAxisInstance();
		getMarkerGraph().getCtx().setXScalar(axisX.getGraphichsScalar().setScale(4, RoundingMode.HALF_UP));
		getMarkerGraph().setMarkerSetHolder(holder);
		getMarkerGraph().addMouseListener(mouseListener);
		getMarkerGraph().setPreferredSize(new Dimension(300, 40));
		getMarkerGraph().initialize();
	}

	
	public MarkerGraph getMarkerGraph() {
		if (markerGraph == null) {
			markerGraph = new MarkerGraph();
		}
		return markerGraph;
	}
	@Override
	public void repaint() {
		if(getGraph() != null && getMarkerGraph() != null){
			AxisInstance axisX = getGraph().getXAxisInstance();
			getMarkerGraph().getCtx().setXScalar(axisX.getGraphichsScalar().setScale(4, RoundingMode.HALF_UP));
		}
		super.repaint();
	}
}
