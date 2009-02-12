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
package org.spantus.exp.segment.beans;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.IThreshold;
/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 *
 */
public class ComparisionResult {
	
	public enum paramEnum{avgTstLength, minTstLength, maxTstLength, 
		avgTstDistance, minTstDistance, maxTstDistance};
	
	String name;
	
	FrameValues sequenceResult;

	FrameValues signal;

	IThreshold threshold;
	
	FrameValues original;
	
	FrameValues test;
	
	float totalResult;
	
	Map<String, BigDecimal> params;
	
	public Map<String, BigDecimal> getParams() {
		if(params == null){
			params = new HashMap<String, BigDecimal>();
		}
		return params;
	}

	public FrameValues getOriginal() {
		return original;
	}

	public void setOriginal(FrameValues original) {
		this.original = original;
	}

	public FrameValues getTest() {
		return test;
	}

	public void setTest(FrameValues test) {
		this.test = test;
	}

	public FrameValues getSequenceResult() {
		return sequenceResult;
	}

	public void setSequenceResult(FrameValues sequenceResult) {
		this.sequenceResult = sequenceResult;
	}

	public float getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(float totalResult) {
		this.totalResult = totalResult;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FrameValues getSignal() {
		return signal;
	}

	public void setSignal(FrameValues signal) {
		this.signal = signal;
	}

	public IThreshold getThreshold() {
		return threshold;
	}

	public void setThreshold(IThreshold threshold) {
		this.threshold = threshold;
	}

}
