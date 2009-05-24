package org.spantus.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.extractor.IExtractorInputReader;

public class MergedWraperExtractorReader extends WraperExtractorReader{
	
	List<Byte> noiseShortBuffer;
	List<Byte> mergedBuffer;
	

	public MergedWraperExtractorReader(IExtractorInputReader reader) {
		super(reader);
		this.noiseShortBuffer = new ArrayList<Byte>();
		this.mergedBuffer = new LinkedList<Byte>();
	}
	@Override
	public void put(byte value) {
		throw new IllegalArgumentException("not impl");
	}
	public void put(byte signalByte, byte noiseByte) {
		float signal,noise = 0;
		mergedBuffer.add((byte)((signalByte+noiseByte)/2));
		switch (format.getSampleSizeInBits()) {
		case 8:
			signal = preemphasis(AudioUtil.read8(signalByte, getFormat()));
			noise = preemphasis(AudioUtil.read8(noiseByte, getFormat()));
			reader.put(sample++, (signal+noise/2));
			break;
		case 16:
			shortBuffer.add(signalByte);
			noiseShortBuffer.add(noiseByte);
			if(shortBuffer.size() == 2){
				signal = AudioUtil.read16(shortBuffer.get(0), 
						shortBuffer.get(1), 
						getFormat());
				noise = AudioUtil.read16(noiseShortBuffer.get(0), 
						noiseShortBuffer.get(1), 
						getFormat());
//				float mergedBytes = AudioUtil.read16(
//								((shortBuffer.get(0)+noiseShortBuffer.get(0))/2),
//								((shortBuffer.get(1)+noiseShortBuffer.get(1))/2),
//						getFormat());
				float f = (signal+noise)/2;
				reader.put(sample++, preemphasis(f));
				shortBuffer.clear();
				noiseShortBuffer.clear();
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
	}
	
	public URL saveMerged(File file, AudioFormat audioFormat){
		InputStream bais = new ByteListInputStream(mergedBuffer);
		AudioInputStream ais = new AudioInputStream(bais, audioFormat, mergedBuffer.size());
	    try {
	    	AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
//	    	log.debug("[saveSegmentAccepted] saved{0}", path);
	    	return file.toURI().toURL();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
