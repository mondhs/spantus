package org.spantus.mpeg7.extractors;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IGeneralExtractor;

public abstract class AbstractMpeg7Extractor implements IGeneralExtractor {

	private float sampleRate = 1f;
//	
//	public FrameValues3D calculate(FrameValues frame) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public abstract FrameVectorValues calculateWindow(FrameValues window);

	public FrameVectorValues getOutputValues() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getDimension() {
		// TODO Auto-generated method stub
		return 0;
	}




	
	public void putValues(Long sample, FrameValues values) {
		// TODO Auto-generated method stub

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
