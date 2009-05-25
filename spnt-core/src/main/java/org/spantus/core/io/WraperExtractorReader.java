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
	Float previousValue;
	
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
	
	protected Float preemphasis(Float currentValue){
		previousValue = previousValue == null?currentValue:previousValue;
		Double val = currentValue - (previousValue*0.95);
		previousValue = currentValue;
		return val.floatValue();
		
	}
	
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
