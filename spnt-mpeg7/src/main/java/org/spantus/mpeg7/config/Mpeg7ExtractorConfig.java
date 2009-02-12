package org.spantus.mpeg7.config;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;

public class Mpeg7ExtractorConfig implements IExtractorConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	float sampleRate;
	int windowSize;
	 Set<String> extractors;
	
	
	public int getBitsPerSample() {
		throw new RuntimeException("not impl");
	}

	
	public int getBufferSize() {
		throw new RuntimeException("not impl");
	}

	
	public int getFrameSize() {
		throw new RuntimeException("not impl");
	}

	
	public float getSampleRate() {
		return sampleRate;
	}

	
	public int getWindowOverlap() {
		throw new RuntimeException("not impl");
	}

	
	public int getWindowSize() {
		return windowSize;
	}

	
	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
		
	}

	
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	
	public Set<String> getExtractors() {
		if(extractors == null){
			extractors = new LinkedHashSet<String>();
		}
		return extractors;
	}

	public void setExtractors(Set<Mpeg7ExtractorEnum> extractors) {
		getExtractors().clear();
		for (Mpeg7ExtractorEnum mpeg7ExtractorEnum : extractors) {
			getExtractors().add(mpeg7ExtractorEnum.name());
		}
	}

	
	public void setBufferSize(int bufferSize) {
		// TODO Auto-generated method stub
		
	}

	
	public void setFrameSize(int frameSize) {
		// TODO Auto-generated method stub
		
	}

	
	public void setWindowOverlap(int windowOverlap) {
		// TODO Auto-generated method stub
		
	}


	public Map<String, ExtractorParam> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getWindowing() {
		return null;
	}


	public void setWindowing(String windowing) {
	}

}
