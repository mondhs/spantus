package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.logger.Logger;

public class ExtractorResultBuffer implements IExtractor {
	Logger log = Logger.getLogger(ExtractorResultBuffer.class);
	IExtractor extractor;

	FrameValues frameValues = new FrameValues();
	FrameValues outputValues = new FrameValues();

	public ExtractorResultBuffer(IExtractor extractor) {
		this.extractor = extractor;
	}
	
	public void putValues(Long sample, FrameValues values) {
		this.frameValues = values;
		calculate(sample, values);
	}

	public FrameValues getFrameValues() {
		return frameValues;
	}
	
	public FrameValues getOutputValues() {
		outputValues.setSampleRate(extractor.getExtractorSampleRate());
		return outputValues;
	}
	public void setOutputValues(FrameValues outputValues) {
		this.outputValues = outputValues;
	}


	public String getName() {
		return "BUFFERED_" + extractor.getName();
	}

	public FrameValues calculateWindow(FrameValues window) {
		throw new RuntimeException("This method should not be called ever. You have to write your own implementation");
	}

	public int getWindowSize() {
		return extractor.getConfig().getWindowSize();
	}


	public FrameValues calculate(Long sample, FrameValues values) {
		FrameValues outputValues = extractor.calculate(sample, getFrameValues());
		getOutputValues().addAll(outputValues);
		int i = getOutputValues().size() - getConfig().getBufferSize();
		while( i > 0 ){
			getOutputValues().poll();
			i--;
		}
		return outputValues;
	}

	
	public IExtractorConfig getConfig() {
		return extractor.getConfig();
	}

	
	public void setConfig(IExtractorConfig config) {
		extractor.setConfig(config);
		
	}

	
	public float getExtractorSampleRate() {
		return extractor.getExtractorSampleRate();
		
	}
	@Override
	public String toString() {
		return getClass().getSimpleName()+ ":" + getName();
	}
	
	public void flush() {
		extractor.flush();		
	}
}
