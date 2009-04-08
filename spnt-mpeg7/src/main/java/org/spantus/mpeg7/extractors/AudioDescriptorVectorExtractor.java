package org.spantus.mpeg7.extractors;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;

public class AudioDescriptorVectorExtractor implements IExtractorVector {
	
	private String name;
	
	private FrameVectorValues values = new FrameVectorValues();

	
	public FrameVectorValues calculate(Long sample, FrameValues frame) {
		return null;
	}

	
	public FrameVectorValues calculateWindow(FrameValues window) {
		return null;
	}

	
	public FrameVectorValues getOutputValues() {
		return values;
	}

	
	public int getDimension() {
		return 0;
	}

	
	public String getName() {
		return this.name;
	}
	



	
	public void putValues(Long sample, FrameValues values) {
		
	}
	
	public void putValues(FrameVectorValues values) {
		this.values.addAll(values);
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return getName() + "; vals: " + values.size();
	}

	public float getSampleRate() {
		return values.getSampleRate();
	}

	public void setSampleRate(float sampleRate) {
		values.setSampleRate(sampleRate);
	}

	
	public IExtractorConfig getConfig() {
		return null;
	}

	
	public float getExtractorSampleRate() {
		return 0;
	}

	
	public void setConfig(IExtractorConfig config) {
	}


	public void flush() {
		// TODO Auto-generated method stub
		
	}

}
