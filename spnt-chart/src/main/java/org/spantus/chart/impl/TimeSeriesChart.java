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
import java.math.BigDecimal;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Graph;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.InteractiveChart;
import org.spantus.chart.SignalSelectionListener;
import org.spantus.chart.functions.FrameValueFuncton;
import org.spantus.core.FrameValues;
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
public class TimeSeriesChart extends AbstractSwingChart{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2779476925528044951L;

	private Graph graph = null;
	
	private FrameValueFuncton function = null;

	public TimeSeriesChart() {
		graph = new InteractiveChart();
		graph.getXAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		graph.getYAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		graph.setBackground(Color.WHITE);
		
		ChartStyle style1 = new ChartStyle();
		style1.setUpperLimitEnabled(true);
		style1.setLowerLimitEnabled(true);
		style1.setPaint(Color.RED);
		function = new FrameValueFuncton("function", new FrameValues(), style1);
		graph.addFunction(function, style1);
		
		add(graph, BorderLayout.CENTER);
		
	}

	public void addFrameValues(FrameValues newvals) {
		function.addFrameValues(newvals);
	}
	
	
	public void repaint() {
		super.repaint();
		if(graph != null){
			graph.render();
			graph.repaint();
		}
	}

	
	public void addSignalSelectionListener(SignalSelectionListener listener) {
		
	}

	@Override
	public void changedZoom(float from, float length) {
		
	}
	
}
