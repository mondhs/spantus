package org.spantus.mpeg7.extractors;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.mpeg7.config.Mpeg7ExtractorConfig;

public class AudioDescriptorExtractor implements IExtractor {
	
	private String name;
	
	private FrameValues values = new FrameValues();
	
	private Mpeg7ExtractorConfig config;
	
	
	public FrameValues calculateWindow(Long sample, FrameValues values) {
		return this.values;
	}

	
	public FrameValues calculateWindow(FrameValues window) {
		return null;
	}

	
	public FrameValues getOutputValues() {
		return values;
	}

	
	public int getDimension() {
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

	
	public Double getExtractorSampleRate() {
		return getConfig().getSampleRate();
	}

	
	public void setConfig(IExtractorConfig config) {
		
	}


	public void flush() {
		
	}


	public long getOffset() {
		return 0;
	}

    @Override
    public String getRegistryName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ExtractorParam getParam() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParam(ExtractorParam ep) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
