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
package org.spantus.exp.segment.services.impl;

import org.spantus.core.FrameValues;
import org.spantus.exp.segment.beans.ComparisionResult;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class MakerComparisonFisherImpl extends MakerComparisonImpl{
	/** 
	 * fisher ratio
	 * 
	 */
	
	protected FrameValues compare(ComparisionResult result){
		FrameValues seq = super.compare(result);
		result.getTest();
		Criteria testCriteria = calculateCriteria(result.getTest());
		Criteria origCriteria = calculateCriteria(result.getOriginal());
		
		Double fisher = Math.pow((testCriteria.mean - origCriteria.mean),2)
		 / Math.pow((testCriteria.variance + origCriteria.variance),2);
		result.setTotalResult(fisher.floatValue());
		log.debug("Fisher comparition result: " + fisher);
		return seq;
	}
	
	public Criteria calculateCriteria(FrameValues vals){
		Criteria c = new Criteria();
		c.mean = 0d;
		Double m2 = 0d;
		long n = 0;
		for (Float x : vals) {
			n++;
			Double delta = x - c.mean; 
			c.mean = (c.mean + delta)/n;
			m2 += delta*(x - c.mean); 
		}
		c.variance = Math.sqrt(m2/(n-1));
		return c;
	}
	
	public class Criteria{
		Double mean;
		Double variance;
	}
}
