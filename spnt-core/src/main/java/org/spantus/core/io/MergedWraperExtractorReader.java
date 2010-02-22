/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.extractor.IExtractorInputReader;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created May 25, 2009
 *
 */
public class MergedWraperExtractorReader extends WraperExtractorReader{
	
	List<Byte> noiseShortBuffer;
	List<Byte> mergedBuffer;
	BigDecimal totalNoiseEnergy = BigDecimal.ZERO.setScale(3);
	BigDecimal totalSignalEnergy = BigDecimal.ZERO.setScale(3);
	

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
		mergedBuffer.add((byte)((signalByte+noiseByte)));
		switch (format.getSampleSizeInBits()) {
		case 8:
			signal = preemphasis(AudioUtil.read8(signalByte, getFormat()));
			noise = preemphasis(AudioUtil.read8(noiseByte, getFormat()));
			reader.put(sample++, (signal+noise));
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
				snrEstimation(signal, noise);
				float f = signal+noise;
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
	
	protected void snrEstimation(float signal, float noise){
		totalNoiseEnergy = totalNoiseEnergy.add(BigDecimal.valueOf(Math.pow(noise, 2)));
		totalSignalEnergy = totalSignalEnergy.add(BigDecimal.valueOf(Math.pow(signal, 2)));
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
	public BigDecimal getTotalNoiseEnergy() {
		return totalNoiseEnergy;
	}
	public BigDecimal getTotalSignalEnergy() {
		return totalSignalEnergy;
	}
	
}
