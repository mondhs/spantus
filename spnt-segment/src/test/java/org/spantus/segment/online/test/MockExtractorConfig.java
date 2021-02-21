package org.spantus.segment.online.test;

import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.threshold.ClassifierEnum;

public class MockExtractorConfig implements IExtractorConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getBitsPerSample() {

		return 0;
	}

	public int getBufferSize() {
		return 100;
	}

	public Set<String> getExtractors() {
		return null;
	}

	public int getFrameSize() {
		return 0;
	}

	public Double getSampleRate() {
		return 0D;
	}

	public int getWindowOverlap() {
		return 0;
	}

	public int getWindowSize() {
		return 0;
	}

	public void setBufferSize(int bufferSize) {
	}

	public void setFrameSize(int frameSize) {
	}

	public void setSampleRate(Double sampleRate) {
	}

	public void setWindowOverlap(int windowOverlap) {
	}

	public void setWindowSize(int windowSize) {
	}

	public Map<String, ExtractorParam> getParameters() {
		return null;
	}

	public String getWindowing() {
		return null;
	}

	public void setWindowing(String windowing) {
	}

	public String getPreemphasis() {
		return null;
	}

	public void setPreemphasis(String preemphasis) {
	}

	@Override
	public ClassifierEnum getClassifier() {
		return null;
	}

}
