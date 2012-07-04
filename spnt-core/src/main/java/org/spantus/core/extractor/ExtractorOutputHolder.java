package org.spantus.core.extractor;

import org.spantus.core.FrameValues;

public class ExtractorOutputHolder implements IExtractor {
	private FrameValues outputValues;
	private String name;
	private IExtractorConfig config;
	private Double extractorSampleRate;


        @Override
	public FrameValues calculateWindow(Long sample, FrameValues values) {
		throw new IllegalAccessError("Should not be called");
	}
        @Override
	public FrameValues calculateWindow(FrameValues window) {
		throw new IllegalAccessError("Should not be called");
	}
        @Override
	public FrameValues getOutputValues() {
		return outputValues;
	}
        @Override
	public IExtractorConfig getConfig() {
		return config;
	}
        @Override
	public Double getExtractorSampleRate() {
		if(extractorSampleRate == null){
			return this.outputValues.getSampleRate();
		}
		return extractorSampleRate;
	}
        @Override
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

	public void setExtractorSampleRate(Double extractorSampleRate) {
		this.extractorSampleRate = extractorSampleRate;
	}

	public void flush() {
		throw new IllegalAccessError("Should not be called");
		//do nothing
	}

	@Override
	public long getOffset() {
		return 0;
	}

}
