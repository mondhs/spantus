package org.spantus.core.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

public class ExtractorVectorOutputHolder implements IExtractorVector {
	private FrameVectorValues outputValues;
	private String name;
	private IExtractorConfig config;


	public FrameVectorValues calculate(Long sample, FrameValues values) {
		throw new IllegalAccessError("Should not be called");
	}

	public FrameVectorValues calculateWindow(FrameValues window) {
		throw new IllegalAccessError("Should not be called");
	}

	public FrameVectorValues getOutputValues() {
		return outputValues;
	}

	public IExtractorConfig getConfig() {
		return config;
	}

	public float getExtractorSampleRate() {
		return this.outputValues.getSampleRate();
	}

	public String getName() {
		return name ;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void putValues(Long sample, FrameValues values) {
		throw new IllegalAccessError("Should not be called");
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}
	public void setOutputValues(FrameVectorValues outputValues) {
		this.outputValues = outputValues;
	}
	
	@Override
	public String toString() {
		
		return getClass().getSimpleName() + "[" +
			getName() +
			" " + getOutputValues().size() + ":" + 
				(getOutputValues().iterator().next().size() ) +
				"]";
	}

}
