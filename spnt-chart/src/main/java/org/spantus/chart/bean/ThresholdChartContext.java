/**
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
package org.spantus.chart.bean;

import net.quies.math.plot.ChartStyle;

import org.spantus.core.FrameValues;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 *
 */
public class ThresholdChartContext {

	FrameValues values;
	
	FrameValues threshold;
	
	FrameValues state;
	
	String description;
	
	ChartStyle style;
	
	Float order;

	public FrameValues getValues() {
		return values;
	}

	public void setValues(FrameValues values) {
		this.values = values;
	}

	public FrameValues getThreshold() {
		return threshold;
	}

	public void setThreshold(FrameValues threshold) {
		this.threshold = threshold;
	}

	public FrameValues getState() {
		return state;
	}

	public void setState(FrameValues state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ChartStyle getStyle() {
		return style;
	}

	public void setStyle(ChartStyle style) {
		this.style = style;
	}

	public Float getOrder() {
		return order;
	}

	public void setOrder(Float order) {
		this.order = order;
	}
}
