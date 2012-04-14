package org.spantus.core.beans;

import org.spantus.core.FrameValues;

public class FrameValuesHolder {

	private FrameValues frameValues;
	
	private Double sampleRate;
	
	public FrameValuesHolder() {
	}
	
	public FrameValuesHolder(FrameValues frameValues) {
		this.sampleRate = frameValues.getSampleRate();
		this.frameValues = frameValues;
	}

	public FrameValues getFrameValues() {
		return frameValues;
	}

	public void setFrameValues(FrameValues frameValues) {
		this.sampleRate = frameValues.getSampleRate();
		this.frameValues = frameValues;
	}

	public Double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		getFrameValues().setSampleRate(sampleRate);
		this.sampleRate = sampleRate;
	}
}
