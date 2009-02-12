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
package org.spantus.chart.functions;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Function;
import net.quies.math.plot.FunctionInstance;
import net.quies.math.plot.GraphDomain;

import org.spantus.chart.bean.ThresholdChartContext;
import org.spantus.chart.impl.ThresholdChartInstance;
import org.spantus.core.FrameValues;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.04
 *
 */
public class FrameValueThearsholdFuncton extends Function {

	private ThresholdChartContext ctx;
	ThresholdChartInstance charType ;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4310600874173414841L;

	public FrameValueThearsholdFuncton(ThresholdChartContext ctx) {
		super(ctx.getDescription());
		charType = new ThresholdChartInstance(ctx);
		this.ctx = ctx;
	}

	public void addFrameValues(FrameValues newvals){
		if(ctx.getValues().size() <= newvals.size()){
			ctx.getValues().clear();
		}else{
			FrameValues tvals = ctx.getValues().subList(newvals.size(), ctx.getValues().size());	
			ctx.getValues().clear();
			ctx.getValues().addAll(tvals);
		}
		
		ctx.getValues().addAll(newvals);
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

	public ThresholdChartInstance getCharType() {
		return charType;
	}

}
