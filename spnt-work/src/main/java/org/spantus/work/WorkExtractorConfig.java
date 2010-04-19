package org.spantus.work;

import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.ExtractorConfig;

public class WorkExtractorConfig implements IExtractorConfig {

	int windowInMills = 100;
	int windowOverlapPercent = 10;
	private String windowing;
	
	private ExtractorConfig config = new ExtractorConfig();
	private String preemphasis;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	public int getBitsPerSample() {
//		return config.getBitsPerSample();
//	}

	public int getBufferSize() {
		return config.getBufferSize();
	}

	public Set<String> getExtractors() {
		return config.getExtractors();
	}

	public int getFrameSize() {
		return config.getFrameSize();
	}

	public float getSampleRate() {
		return config.getSampleRate();
	}

	public int getWindowOverlap() {
		return config.getWindowOverlap();
	}

	public int getWindowSize() {
		return config.getWindowSize();
	}

	public void setBufferSize(int bufferSize) {
		config.setBufferSize(bufferSize);

	}

	public void setFrameSize(int frameSize) {
		config.setFrameSize(frameSize);
	}

	public void setSampleRate(float sampleRate) {
		setWindowInMills(windowInMills);
		config.setSampleRate(sampleRate);
	}

	public void setWindowOverlap(int windowOverlap) {
		config.setWindowOverlap(windowOverlap);
	}

	public void setWindowSize(int windowSize) {
		config.setWindowSize(windowSize);
	}

	public int getWindowInMills() {
		return windowInMills;
	}

	public void setWindowInMills(int windowInMills) {
		config.setWindowSize((int)(config.getSampleRate()/windowInMills));
		setWindowOverlapPercent(windowOverlapPercent);
		this.windowInMills = windowInMills;
	}

	public int getWindowOverlapPercent() {
		return windowOverlapPercent;
	}

	public void setWindowOverlapPercent(int windowOverlapPercent) {
		config.setWindowOverlap(getWindowSize()-((getWindowSize()*(windowOverlapPercent/2)))/100);
		this.windowOverlapPercent = windowOverlapPercent;
	}

	public Map<String, ExtractorParam> getParameters() {
		return null;
	}

	public ExtractorConfig getConfig() {
		return config;
	}

	public void setConfig(ExtractorConfig config) {
		this.config = config;
	}

	public String getWindowing() {
		return windowing;
	}
	
	public void setWindowing(String windowing) {
		this.windowing = windowing;
	}

	public String getPreemphasis() {
		return preemphasis;
	}

	public void setPreemphasis(String preemphasis) {
		this.preemphasis = preemphasis;
	}

}
