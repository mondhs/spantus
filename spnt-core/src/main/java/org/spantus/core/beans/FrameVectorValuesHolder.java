package org.spantus.core.beans;

import org.spantus.core.FrameVectorValues;

public class FrameVectorValuesHolder implements IValueHolder<FrameVectorValues>{

	FrameVectorValues values;
	
	/**
	 * Sample rate cannot be null
	 */
	Double sampleRate;
	
	public FrameVectorValuesHolder() {
	}
	
	public FrameVectorValuesHolder(FrameVectorValues frameVectorValues) {
		this.sampleRate = frameVectorValues.getSampleRate();
		this.values = frameVectorValues;
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
	public FrameVectorValues getValues() {
		return values;
	}

	@Override
	public void setValues(FrameVectorValues values) {
		this.sampleRate = values.getSampleRate();
		this.values = values;
		if(values != null && values.getSampleRate() != null){
			this.sampleRate = values.getSampleRate();
		}
	}

}
