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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
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
 *        Created 2008.04.11
 * 
 */
public class DefaultAudioReader extends AbstractAudioReader {
	Logger log = Logger.getLogger(getClass());

	WraperExtractorReader wraperExtractorReader;
	
	public void readAudio(URL url, IExtractorInputReader bufferedReader) {
		wraperExtractorReader = createWraperExtractorReader(bufferedReader);
		wraperExtractorReader.setFormat(getAudioFormat(url).getFormat());
		readAudio(url, wraperExtractorReader);
	}
	
	public WraperExtractorReader createWraperExtractorReader(IExtractorInputReader bufferedReader){
		return new WraperExtractorReader(bufferedReader);
	}
	
	public void readAudio(URL url, WraperExtractorReader wraperExtractorReader) {
		this.wraperExtractorReader = wraperExtractorReader;
		try {
			readAudioInternal(url);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	public void readAudioInternal(URL url)
			throws UnsupportedAudioFileException, IOException {
		AudioFileFormat audioFileFormat= AudioSystem.getAudioFileFormat(url);
		DataInputStream dis = new DataInputStream(new BufferedInputStream(
				AudioSystem.getAudioInputStream(url)));
		Long size = Long.valueOf(audioFileFormat.getFrameLength()*audioFileFormat.getFormat().getFrameSize()); 
		started(size);
		for (long index = 0; index < size; index++) {
			int readByte = dis.read();
			if(readByte == -1) break;
			wraperExtractorReader.put((byte)readByte);
			processed(Long.valueOf(index), size);
		}
		wraperExtractorReader.pushValues();
		dis.close();
		ended();

	}

	public AudioFileFormat getAudioFormat(URL url) {
		try {
			return AudioSystem.getAudioFileFormat(url);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	public WraperExtractorReader getWraperExtractorReader() {
		return wraperExtractorReader;
	}
}
