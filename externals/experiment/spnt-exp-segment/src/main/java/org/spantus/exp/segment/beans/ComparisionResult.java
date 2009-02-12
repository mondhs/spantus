package org.spantus.exp.segment.beans;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.IThreshold;

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
