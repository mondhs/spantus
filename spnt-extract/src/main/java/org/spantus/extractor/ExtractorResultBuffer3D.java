package org.spantus.extractor;

import java.util.Collections;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.logger.Logger;

public class ExtractorResultBuffer3D implements IExtractorVector {
	
	Logger log = Logger.getLogger(ExtractorResultBuffer3D.class);
	
	IExtractorVector extractor;
	
	FrameValues frameValues = new FrameValues();
	FrameVectorValues outputValues = new FrameVectorValues();
	IExtractorConfig config;

	public ExtractorResultBuffer3D(IExtractorVector extractor) {
		this.extractor = extractor;
	}

	
	public void putValues(Long sample, FrameValues values) {
		this.frameValues = values;
		calculate(sample, values);
	}

	public FrameValues getFrameValues() {
		return frameValues;
	}
	
	public FrameVectorValues getOutputValues() {
		outputValues.setSampleRate(extractor.getExtractorSampleRate());
		return outputValues;
	}
	public void setOutputValues(FrameVectorValues outputValues) {
		this.outputValues = outputValues;
	}

	public ExtractorParam getParam() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setParam(ExtractorParam param) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "BUFFERED_" + extractor.getName();
	}

//	public int getWinowSize() {
//		return extractor.getConfig().getWindowSize();
//	}

	public FrameVectorValues calculate(Long sample, FrameValues values) {
		FrameVectorValues outputValues = extractor.calculate(sample, getFrameValues());
		
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


	
	public FrameVectorValues calculateWindow(FrameValues window) {
		// TODO Auto-generated method stub
		return null;
	}


	
	public float getExtractorSampleRate() {
		return getConfig().getSampleRate();
	}

}
