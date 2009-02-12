package org.spantus.core.threshold.test;

import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;

public class MockExtractorConfig implements IExtractorConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getBitsPerSample() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBufferSize() {
		return 100;
	}

	public Set<String> getExtractors() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getFrameSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getSampleRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWindowOverlap() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWindowSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBufferSize(int bufferSize) {
		// TODO Auto-generated method stub

	}

	public void setFrameSize(int frameSize) {
		// TODO Auto-generated method stub

	}

	public void setSampleRate(float sampleRate) {
		// TODO Auto-generated method stub

	}

	public void setWindowOverlap(int windowOverlap) {
		// TODO Auto-generated method stub

	}

	public void setWindowSize(int windowSize) {
		// TODO Auto-generated method stub

	}

	public Map<String, ExtractorParam> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWindowing() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setWindowing(String windowing) {
		// TODO Auto-generated method stub
		
	}

}
