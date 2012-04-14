package org.spantus.core.beans;

import org.spantus.core.FrameVectorValues;

public class FrameVectorValuesHolder {

	FrameVectorValues frameVectorValues;
	
	Double sampleRate;
	
	public FrameVectorValuesHolder() {
	}
	
	public FrameVectorValuesHolder(FrameVectorValues frameVectorValues) {
		this.sampleRate = frameVectorValues.getSampleRate();
		this.frameVectorValues = frameVectorValues;
	}

	public FrameVectorValues getFrameVectorValues() {
		return frameVectorValues;
	}

	public void setFrameVectorValues(FrameVectorValues frameVectorValues) {
		this.sampleRate = frameVectorValues.getSampleRate();
		this.frameVectorValues = frameVectorValues;
	}

	public Double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		getFrameVectorValues().setSampleRate(sampleRate);
		this.sampleRate = sampleRate;
	}
}
