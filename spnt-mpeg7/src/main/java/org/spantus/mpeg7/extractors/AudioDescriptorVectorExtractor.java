package org.spantus.mpeg7.extractors;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;

public class AudioDescriptorVectorExtractor implements IExtractorVector {
	
	private String name;
	
	private FrameVectorValues values = new FrameVectorValues();

	
	public FrameVectorValues calculateWindow(Long sample, FrameValues frame) {
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

	public Double getSampleRate() {
		return values.getSampleRate();
	}

	public void setSampleRate(Double sampleRate) {
		values.setSampleRate(sampleRate);
	}

	
	public IExtractorConfig getConfig() {
		return null;
	}

	
	public Double getExtractorSampleRate() {
		return 0D;
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
