package org.spantus.exp.segment.services.impl;

import org.spantus.core.FrameValues;
import org.spantus.exp.segment.beans.ComparisionResult;

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
