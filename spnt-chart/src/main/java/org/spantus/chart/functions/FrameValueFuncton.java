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

import org.spantus.chart.impl.SignalChartInstance;
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
public class FrameValueFuncton extends Function {

	private FrameValues vals = new FrameValues();
	SignalChartInstance charType ;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4310600874173414841L;

	public FrameValueFuncton(String description, FrameValues vals, ChartStyle style) {
		super(description);
		charType = new SignalChartInstance(description, vals, style);
		this.vals = vals;
	}

	public void addFrameValues(FrameValues newvals){
		if(vals.size() <= newvals.size()){
			vals.clear();
		}else{
			FrameValues tvals = vals.subList(newvals.size(), vals.size());	
			vals.clear();
			vals.addAll(tvals);
		}
		
		vals.addAll(newvals);
	}
	
	
	public FunctionInstance getInstance(GraphDomain domain, ChartStyle style) {
//		BigDecimal from = domain.getFrom();
//		BigDecimal until = domain.getUntil();
//		if (from == null) {
//			if (until == null)
//				return getInstance(vals, style);
//			return getInstance(vals.subList(0, until.intValue()), style);
//		}
//		if (until == null)
//			return getInstance(vals.subList(from.intValue(),vals.size()-1), style);
//		return getInstance(vals.subList(from.intValue(),until.intValue()), style);
		charType.setDomain(domain);
		return charType;

	}


	public float getOrder() {
		return charType.getOrder();
	}

	public void setOrder(float order) {
		charType.setOrder(order);
	}

	public SignalChartInstance getCharType() {
		return charType;
	}

}
