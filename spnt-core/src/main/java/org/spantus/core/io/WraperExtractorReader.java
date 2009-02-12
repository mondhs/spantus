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
	Float amplitude;
	
	public WraperExtractorReader(IExtractorInputReader reader) {
		this.reader = reader;
		this.shortBuffer = new ArrayList<Byte>();
		sample = 0L;
	}	
	
	public void put(byte value){
		switch (format.getSampleSizeInBits()) {
		case 8:
				reader.put(sample++, AudioUtil.read8(value, getFormat()) / getAmplitude());
				break;
		case 16:
			shortBuffer.add(value);
			if(shortBuffer.size() == 2){
				float f = AudioUtil.read16(shortBuffer.get(0), 
						shortBuffer.get(1), 
						getFormat())/getAmplitude();
				reader.put(sample++, f);
				shortBuffer.clear();
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
		
	}
	public void pushValues(){
		reader.pushValues(sample);
	}
	public void setFormat(AudioFormat format) {
		float a = 1 << (format.getSampleSizeInBits() - 1);
		amplitude = a;
		this.format = format;
	}
	public Float getAmplitude() {
		if(amplitude == null ){
			return 1f;
		}
		return amplitude;
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
