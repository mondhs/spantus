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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
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
	
	public void readSignal(URL url, IExtractorInputReader bufferedReader) {
		wraperExtractorReader = createWraperExtractorReader(bufferedReader);
		wraperExtractorReader.setFormat(getCurrentAudioFormat(url));
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
	public AudioFormat getCurrentAudioFormat(URL url) {
		return getAudioFormat(url).getFormat();
	}
	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.io.AudioReader#getAudioFormat(java.net.URL)
	 */
	public AudioFileFormat getAudioFormat(URL url) {
		AudioFileFormat format = null;
		try {
			format = AudioSystem.getAudioFileFormat(url);
		} catch (UnsupportedAudioFileException e) {
		} catch (IOException e) {
			log.debug("[getAudioFormat]IO exception Exception " + url.getFile());
		}
		return format;
	}

	public Float getSampleRate(URL url) {
		try {
			return AudioSystem.getAudioFileFormat(url).getFormat().getSampleRate();
		} catch (UnsupportedAudioFileException e) {
			return 1F;
		} catch (IOException e) {
			return 1F;
		}
		
	}
	
	public WraperExtractorReader getWraperExtractorReader() {
		return wraperExtractorReader;
	}

	protected Map<String, Object> extractParameters(AudioFileFormat audioFileFormat, URL url){
		Map<String, Object> parameters = new LinkedHashMap<String, Object>();
		//ui representaion format
		parameters.put("file", url);
		parameters.put("type", audioFileFormat.getType().toString());
		parameters.put("sampleRateInHz", audioFileFormat.getFormat().getSampleRate());
		parameters.put("resolutionInBits", audioFileFormat.getFormat().getSampleSizeInBits());
		parameters.put("encoding", audioFileFormat.getFormat().getEncoding());
		parameters.put("encoding", audioFileFormat.getFormat().getEncoding());
		parameters.put("channels", audioFileFormat.getFormat().getChannels());
		parameters.put("bigEdian", audioFileFormat.getFormat().isBigEndian());
		parameters.put("bytes", audioFileFormat.getByteLength());
		
		
		return parameters;
	}
	
	public SignalFormat getFormat(URL url) {
		SignalFormat signalFormat = new SignalFormat();
		AudioFileFormat audioFileFormat = getAudioFormat(url);
		signalFormat.setLength(audioFileFormat.getFrameLength());
		signalFormat.setSampleRate(audioFileFormat.getFormat().getSampleRate());
		signalFormat.setParameters(extractParameters(audioFileFormat, url));
		return signalFormat;
	}

	public boolean isFormatSupported(URL url) {
		AudioFileFormat audioFileFormat = getAudioFormat(url);
		return audioFileFormat != null;
	}

	
}
