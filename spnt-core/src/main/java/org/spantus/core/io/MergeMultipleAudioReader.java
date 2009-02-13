/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.core.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created 2008.04.11
 * 
 */
public class MergeMultipleAudioReader extends DefaultAudioReader implements MultipleAudioReader {
	Logger log = Logger.getLogger(getClass());

	
	public void readAudio(URL mainSignal, URL noisesUrl, IExtractorInputReader bufferedReader) {
		try {
			readAudioInternal(mainSignal, noisesUrl, bufferedReader);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	public void readAudioInternal(URL mainSignal, URL noisesUrl, IExtractorInputReader reader)
			throws UnsupportedAudioFileException, IOException {

//		AudioFileFormat mainFormat = getAudioFormat(mainSignal);
//		AudioFileFormat noiseFormat = getAudioFormat(noisesUrl);
////		if(mainFormat.getFormat().getSampleSizeInBits() != noiseFormat.getFormat().getSampleSizeInBits() ){
////			throw new IllegalArgumentException(
////					"There is no impl for merging 2 different formats " + 
////					mainFormat.getFormat().getSampleSizeInBits() != noiseFormat.getFormat().getSampleSizeInBits());
////		}
//		
//		DataInputStream mainDis = new DataInputStream(new BufferedInputStream(
//				AudioSystem.getAudioInputStream(mainSignal)));
//		DataInputStream noiseDis = new DataInputStream(new BufferedInputStream(
//				AudioSystem.getAudioInputStream(noisesUrl)));		
//
//		int index = 0;
//		int bitsPerSample = mainFormat.getFormat().getSampleSizeInBits();
//		int size = mainDis.available();
//
//		float amplitude = 1 << (bitsPerSample - 1);
////		try {
////			switch (bitsPerSample) {
////			case 8:
////				for (index = 0; index < size; ++index)
//////					reader.put( read8(mainDis, noiseDis, noiseFormat)/ amplitude);
////				break;
////			case 16:
////				for (index = 0; index < (size/2); ++index){
//////					float f = read16(mainDis, noiseDis, noiseFormat) / amplitude;
//////					reader.put(f);
////				}
////				break;
////			default:
////				throw new java.lang.IllegalArgumentException(bitsPerSample
////						+ " bits/sample not supported");
////
////			}
////		} catch (EOFException e) {
////			throw new ProcessingException("try to read size of sample: " + size + ", eof on index: " + index, e);
////		}finally{
////			reader.pushValues();
////		}

	}
	protected float readNoise(DataInputStream noiseDis, AudioFileFormat noiseFormat) throws IOException{
		float noiseValue = 0; 
		switch (noiseFormat.getFormat().getSampleSizeInBits()) {
		case 8:
//			noiseValue = super.read8(noiseDis, noiseFormat.getFormat());
			break;
		case 16:
//			noiseValue = super.read16(noiseDis, noiseFormat.getFormat());
			break;
		}
		return noiseValue;
	}
	
//	protected float read8(DataInputStream mainDis, DataInputStream noiseDis, AudioFileFormat noiseFormat) throws IOException{
//		float noiseValue = readNoise(noiseDis, noiseFormat);
//		return (super.read8(mainDis, noiseFormat.getFormat())+noiseValue)/2;
//	}
//	protected float read16(DataInputStream mainDis, DataInputStream noiseDis, AudioFileFormat noiseFormat) throws IOException{
//		float noiseValue = readNoise(noiseDis, noiseFormat);
//		return (super.read16(mainDis, noiseFormat.getFormat())+noiseValue)/2;
//	}

	public AudioFileFormat getAudioFormat(URL mainSignal) {
		try {
			return AudioSystem.getAudioFileFormat(mainSignal);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

}
