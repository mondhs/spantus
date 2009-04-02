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
package org.spantus.chart.functions;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Function;
import net.quies.math.plot.FunctionInstance;
import net.quies.math.plot.GraphDomain;

import org.spantus.chart.impl.AreaChartInstance;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

public class FrameValueAreaFunction extends Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4770519604910040851L;

	AreaChartInstance charType;

	public FrameValueAreaFunction(String description, FrameVectorValues vals,
			ChartStyle style) {
		super(description);
		charType = new AreaChartInstance(description, vals, style);
	}

	public void addFrameValues(FrameValues newvals) {
		throw new RuntimeException("Not impl");
	}

	
	public FunctionInstance getInstance(GraphDomain domain, ChartStyle style) {
		charType.setDomain(domain);
		return charType;
	}
	public void setOrder(float order) {
		charType.setOrder(order);
	}

	public AreaChartInstance getCharType() {
		return charType;
	}
}
