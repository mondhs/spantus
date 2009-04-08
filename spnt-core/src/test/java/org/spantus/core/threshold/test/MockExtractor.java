package org.spantus.core.threshold.test;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;

public class MockExtractor implements IExtractor {

	IExtractorConfig config;
	
	public FrameValues calculate(Long sample,FrameValues values) {
		return calculateWindow(values);
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues rtn = new FrameValues();
		float avg = 0;
		for (Float float1 : window) {
			avg += float1;
		}
		avg /= window.size();
		rtn.add(avg);
		return rtn;
	}

	public FrameValues getOutputValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtractorConfig getConfig() {
		return config;
	}

	public int getDimension() {
		// TODO Auto-generated method stub
		return 0;
	}
	float extractorSampleRate;

	public float getExtractorSampleRate() {
		return extractorSampleRate;
	}

	public void setExtractorSampleRate(float extractorSampleRate) {
		this.extractorSampleRate = extractorSampleRate;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void putValues(Long sample, FrameValues values) {
		// TODO Auto-generated method stub

	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}

	public void flush() {
		// TODO Auto-generated method stub
		
	}

}
