package org.spantus.core.io;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorInputReader;

public class WraperExtractorReader {
	AudioFormat format;
	IExtractorInputReader reader;
	List<Byte> shortBuffer;
	
	Long sample;
//	Float amplitude;
	Float previousValueZ1 = 0F;
	Float previousValueZ2 = 0F;
	
	public WraperExtractorReader(IExtractorInputReader reader) {
		this.reader = reader;
		this.shortBuffer = new ArrayList<Byte>();
		sample = 0L;
	}	
	
	public void put(byte value){
		switch (format.getSampleSizeInBits()) {
		case 8:
				reader.put(sample++, preemphasis( 
						AudioUtil.read8(value, getFormat()) 
						));
				break;
		case 16:
			shortBuffer.add(value);
			if(shortBuffer.size() == 2){
				float f = AudioUtil.read16(shortBuffer.get(0), 
						shortBuffer.get(1), 
						getFormat());
				reader.put(sample++, preemphasis(f));
				shortBuffer.clear();
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
		
	}
//	protected Float preemphasis(Float currentValue){
//		return currentValue;
//		
//	}
	
	protected Float preemphasis(Float currentValue){
		Double val = currentValue.doubleValue();
		val -=  (previousValueZ1*0.95);
		previousValueZ1 = currentValue;
		return val.floatValue();
		
	}
	/**
	 * 		y[n] = b0 x[n] + b1 x[n-1] + b2 x[n-2]
	 *	b0 = 0.3426, b1 = 0.4945 and b2 = -0.64
	 */
//	protected Float preemphasis(Float currentValue){
//		Double val = currentValue.doubleValue();
//		float b0 = 0.3426f, b1 = 0.4945f, b2 = -0.64f;
//		val = b0*val+b1*previousValueZ1+b2*previousValueZ2;
//		previousValueZ2 = previousValueZ1;
//		previousValueZ1 = currentValue;
//		return val.floatValue();
//		
//	}
	
	public void pushValues(){
		reader.pushValues(sample);
	}
	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public IExtractorInputReader getReader() {
		return reader;
	}

	public Long getSample() {
		return sample;
	}

	
}
