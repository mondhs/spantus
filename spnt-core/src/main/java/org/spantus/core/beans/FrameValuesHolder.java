package org.spantus.core.beans;

import org.spantus.core.FrameValues;

public class FrameValuesHolder implements IValueHolder<FrameValues>{

	private FrameValues values;
	
	private Double sampleRate;
	
	public FrameValuesHolder() {
	}
	
	public FrameValuesHolder(FrameValues frameValues) {
		this.sampleRate = frameValues.getSampleRate();
		this.values = frameValues;
	}

	public Double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		if(getValues() != null && sampleRate!=null ){
			getValues().setSampleRate(sampleRate);
		}
		this.sampleRate = sampleRate;
	}

	@Override
	public FrameValues getValues() {
		return values;
	}

	@Override
	public void setValues(FrameValues values) {
		this.sampleRate = values.getSampleRate();
		this.values = values;
		if(values != null && values.getSampleRate() != null){
			this.sampleRate = values.getSampleRate();
		}
	}
}
