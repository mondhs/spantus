package org.spantus.core.extractor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;

public class DefaultExtractorConfig implements IExtractorConfig{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int bufferSize;
	
	private int frameSize;

	private int windowSize;
	
	private int windowOverlap;
	
	private float sampleRate;
	
	private int bitsPerSample;
	
	private String windowing;
	
	private Set<String> extractors;
	
	private Map<String, ExtractorParam> parameters;

	private String preemphasis;
	
	public DefaultExtractorConfig() {
		sampleRate = 44000;
		windowSize = 512;
		windowOverlap = windowSize - (windowSize / 10); 
		frameSize = (windowSize * 10)+windowOverlap;
		bufferSize = 850;
	}
	
	public DefaultExtractorConfig(IExtractorConfig cloning) {
		this.sampleRate = cloning.getSampleRate();
		this.windowSize = cloning.getWindowSize();
		this.windowOverlap = cloning.getWindowOverlap();
		this.frameSize = cloning.getFrameSize();
		this.bufferSize = cloning.getBufferSize();
		this.extractors = cloning.getExtractors();
		this.parameters = cloning.getParameters();
		this.windowing = cloning.getWindowing();
		this.bitsPerSample = cloning.getBitsPerSample();
	}
	
	
	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(int bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getWindowOverlap() {
		return windowOverlap;
	}

	public void setWindowOverlap(int windowOverlap) {
		this.windowOverlap = windowOverlap;
	}

	
	public Set<String> getExtractors() {
		if(extractors == null){
			extractors = new HashSet<String>();
		}
		return extractors;
	}

	public Map<String, ExtractorParam> getParameters() {
		if(parameters == null){
			parameters = new HashMap<String, ExtractorParam>();
		}
		return parameters;
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
