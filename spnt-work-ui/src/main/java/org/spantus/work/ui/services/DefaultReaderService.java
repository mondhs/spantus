/*
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
package org.spantus.work.ui.services;

import java.net.URL;

import javax.sound.sampled.AudioFileFormat;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.io.AudioFactory;
import org.spantus.work.reader.MultiFeatureExtractorInputReader;
import org.spantus.work.ui.dto.FeatureReader;
import org.spantus.work.ui.util.WorkUIExtractorConfigUtil;

/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created Jun 11, 2008
 * 
 */
public class DefaultReaderService implements ReaderService {

	
	public IExtractorInputReader getReader(URL url, FeatureReader readerDto, 
			ProcessedFrameLinstener processedFrameLinstener) {
		IExtractorInputReader extractor = null;
		switch (readerDto.getReaderPerspective()) {
//		case simple:
//			extractor = new SimpleExtractorInputReader();
//			setConfig(url, extractor, readerDto);
//			break;
		case multiFeature:
			extractor = new MultiFeatureExtractorInputReader();
			setConfig(url, extractor, readerDto);
			break;
		default:
			throw new RuntimeException("Not implemented:"
					+ readerDto.getReaderPerspective());
		}
		
		AudioReader audioReader = AudioFactory.createAudioReader(readerDto.getReaderPerspective());
		if(processedFrameLinstener != null && audioReader instanceof ProcessedFrameLinstener){
			((ProcessedFrameLinstener)audioReader).registerProcessedFrameLinstener(processedFrameLinstener);
		}
		audioReader.readAudio(url, extractor);
		return extractor;
	}
	public AudioFileFormat getFormat(URL url){
		return AudioFactory.createAudioReader(WorkReadersEnum.multiFeature).getAudioFormat(url);
	}
	
	protected void setConfig(URL url, IExtractorInputReader extractor,FeatureReader readerDto){
		AudioFileFormat format = AudioFactory.createAudioReader(readerDto.getReaderPerspective())
		.getAudioFormat(url);
		IExtractorConfig config = WorkUIExtractorConfigUtil.convert(readerDto.getWorkConfig(), format.getFormat().getSampleRate());
		config.getExtractors().addAll(readerDto.getExtractors());
		config.getParameters().putAll(readerDto.getParameters());
		extractor.setConfig(config);

	}

}
