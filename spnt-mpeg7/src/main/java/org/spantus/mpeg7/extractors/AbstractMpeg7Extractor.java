package org.spantus.mpeg7.extractors;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IGeneralExtractor;

public abstract class AbstractMpeg7Extractor implements IGeneralExtractor<FrameVectorValues> {

	private float sampleRate = 1f;


	public FrameVectorValues getOutputValues() {
		return null;
	}

	
	public int getDimension() {
		return 0;
	}




	
	public void putValues(Long sample, FrameValues values) {
	}


	protected FrameVectorValues createFrameValueVector(){
		return new FrameVectorValues();
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	public void flush() {
		
	}

}
