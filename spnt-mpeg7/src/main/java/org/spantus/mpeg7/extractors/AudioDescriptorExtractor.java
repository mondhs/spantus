package org.spantus.mpeg7.extractors;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.mpeg7.config.Mpeg7ExtractorConfig;

public class AudioDescriptorExtractor implements IExtractor {
	
	private String name;
	
	private FrameValues values = new FrameValues();
	
	private Mpeg7ExtractorConfig config;
	
	
	public FrameValues calculate(Long sample, FrameValues values) {
		return this.values;
	}

	
	public FrameValues calculateWindow(FrameValues window) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public FrameValues getOutputValues() {
		return values;
	}

	
	public int getDimension() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}



	
	public void putValues(Long sample, FrameValues values) {
		this.values.addAll(values);
	}

	
	public String toString() {
		return getName() + "; vals: " + values.size();
	}

	
	public IExtractorConfig getConfig() {
		if(config == null){
			config = new Mpeg7ExtractorConfig();
		}
		return config;
	}

	
	public float getExtractorSampleRate() {
		return getConfig().getSampleRate();
	}

	
	public void setConfig(IExtractorConfig config) {
		// TODO Auto-generated method stub
		
	}


}
