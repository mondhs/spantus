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

import org.spantus.chart.impl.WavMatrixChartInstance;
import org.spantus.core.FrameVectorValues;
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
public class FrameValueMatrixFuncton extends Function {

	private FrameVectorValues vals = new FrameVectorValues();
	WavMatrixChartInstance charType ;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4310600874173414841L;

	public FrameValueMatrixFuncton(String description, FrameVectorValues vals) {
		super(description);
		charType = new WavMatrixChartInstance(description, vals);
		this.vals = vals;
	}

	public void addFrameValues(FrameVectorValues newvals){
		if(vals.size() <= newvals.size()){
			vals.clear();
		}else{
			FrameVectorValues tvals = vals.subList(newvals.size(), vals.size());	
			vals.clear();
			vals.addAll(tvals);
		}
		
		vals.addAll(newvals);
	}
	
	
	public FunctionInstance getInstance(GraphDomain domain, ChartStyle style) {
		charType.setDomain(domain);
		return charType;
	}

	public Double getOrder() {
		return charType.getOrder();
	}

	public void setOrder(Double order) {
		charType.setOrder(order);
	}

	public WavMatrixChartInstance getCharType() {
		return charType;
	}

}
