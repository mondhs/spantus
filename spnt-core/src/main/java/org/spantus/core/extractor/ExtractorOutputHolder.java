package org.spantus.core.extractor;

import org.spantus.core.FrameValues;

public class ExtractorOutputHolder implements IExtractor {
	private FrameValues outputValues;
	private String name;
	private IExtractorConfig config;
//	private float extractorSampleRate;


	public FrameValues calculate(Long sample, FrameValues values) {
		throw new IllegalAccessError("Should not be called");
	}

	public FrameValues calculateWindow(FrameValues window) {
		throw new IllegalAccessError("Should not be called");
	}

	public FrameValues getOutputValues() {
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
	public void setOutputValues(FrameValues outputValues) {
		this.outputValues = outputValues;
	}

}
