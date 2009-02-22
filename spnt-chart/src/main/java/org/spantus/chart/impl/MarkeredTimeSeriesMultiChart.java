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
		super.repaint();
		if(getGraph() != null && getMarkerGraph() != null){
			AxisInstance axisX = getGraph().getXAxisInstance();
			getMarkerGraph().getCtx().setXScalar(axisX.getGraphichsScalar().setScale(4, RoundingMode.HALF_UP));
			getMarkerGraph().resetScreenCoord();
		}
		
	}
	@Override
	public void changedZoom(float from, float length) {
		super.changedZoom(from, length);
		repaint();
		getMarkerGraph().resetScreenCoord();
//		getMarkerGraph().resize(getSize());
		repaint(); 
	}
}
