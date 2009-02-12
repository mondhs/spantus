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
package org.spantus.chart;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import net.quies.math.plot.AxisInstance;
import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphInstance;
import net.quies.math.plot.InteractiveGraph;
import net.quies.math.plot.ToolBar;
import net.quies.math.plot.XAxis;
import net.quies.math.plot.YAxis;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.04.11
 * 
 */
public class InteractiveChart extends InteractiveGraph {
	SpantusChartToolbar spntToolbar;
	List<ChartDescriptionResolver> resolvers;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InteractiveChart() {
		super(new XAxis(), new YAxis(), new Insets(0, 0, 65, 0));
	}

	
	public ToolBar getToolBar() {
		if (spntToolbar == null) {
			spntToolbar = new SpantusChartToolbar(this);
		}
		return spntToolbar;
	}

	public String getToolTipText(MouseEvent event) {
		GraphInstance render = getRender();
		if (render == null)
			return "";
		Point mousePosition = event.getPoint();
		StringBuffer buffer = new StringBuffer(80);
		buffer.append('(');
		buffer.append(getXAxis().getFormat().format(
				render.getXValue(mousePosition.x)));
		buffer.append(", ");
		buffer.append(resolveChart(mousePosition.y));
		buffer.append(')');
		return buffer.toString();
	}

	private String resolveChart(int y) {
		GraphInstance render = getRender();
		List<String> descs = new ArrayList<String>();
		for (ChartDescriptionResolver resolver : getResolvers()) {
			ChartDescriptionInfo resolved = resolver
					.resolve(render.getYValue(y).floatValue());
			if (resolved != null) {
				descs.add(resolved.getName());
			}
		}
		if (descs.size() == 0) {
			return getYAxis().getFormat().format(render.getYValue(y));
		}
		return descs.toString();
	}

	public List<ChartDescriptionResolver> getResolvers() {
		if (resolvers == null) {
			resolvers = new ArrayList<ChartDescriptionResolver>();
		}
		return resolvers;
	}

	public void addResolver(ChartDescriptionResolver resolver) {
		getResolvers().add(resolver);
	}

	public void mousePressed(MouseEvent event) {
		if(spntToolbar == null || spntToolbar.getChartInfo().isSelfZoomable()){
			super.mousePressed(event);
			return;
		}
		int modifiers = event.getModifiers();
		if ((modifiers & InputEvent.BUTTON1_MASK) != 0){
				setZoomSelection(new SpantusChartZoomSelection(this, event.getPoint()));
		}
	}

	public void mouseReleased(MouseEvent event) {
		if(spntToolbar == null || spntToolbar.getChartInfo().isSelfZoomable()){
			super.mouseReleased(event);
			return;
		}
		SpantusChartZoomSelection spntZoomSelection = (SpantusChartZoomSelection)getZoomSelection();
		int modifiers = event.getModifiers();
		if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
			if (getZoomSelection() != null) {
				boolean selected = spntZoomSelection.apply();
				if (selected)
					getToolBar().setZoom(
							spntZoomSelection.getIntervalDescription());
				else
					getToolBar().setZoomPending(null);
				 notifyZoomListeners(spntZoomSelection.getZoomDomain());
			}
		}
	}
	/**
	 * 
	 * @return
	 */
	protected int getGraphWidth(){
		Insets border = getInsets();
		Insets padding = getPadding();	// thread-safe
		int right = border.right + padding.right;
		int left = border.left + padding.left;
		int width = getWidth() - left - right;
		return width;
	}

	
	public AxisInstance getXAxisInstance(){
		int width = getWidth();
		width = width == 0 ? 1:width;
		CoordinateBoundary boundary = getCoordinateBoundary();
		AxisInstance xAxis = getXAxis().getInstance(boundary.getXMin(), boundary.getXMax(), 
				width);
//		BigDecimal xScalar = xAxis.getGraphichsScalar();
		return xAxis;
	}	


}
