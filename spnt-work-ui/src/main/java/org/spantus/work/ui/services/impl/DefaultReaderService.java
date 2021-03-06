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
package org.spantus.work.ui.services.impl;

import java.net.URL;
import java.util.List;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.io.SignalReader;
import org.spantus.logger.Logger;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.io.WorkAudioFactory;
import org.spantus.work.reader.MultiFeatureExtractorInputReader;
import org.spantus.work.ui.dto.FeatureReader;
import org.spantus.work.ui.services.ReaderService;
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
	Logger log = Logger.getLogger(DefaultReaderService.class);
	
	
	public IExtractorInputReader read(List<URL> urls, FeatureReader readerDto, 
			ProcessedFrameLinstener processedFrameLinstener) {
		log.debug("[read]reading:" + urls);
		IExtractorInputReader extractor = null;
		SignalReader signalReader = createSignalReader(urls, readerDto.getReaderPerspective());
		
		switch (readerDto.getReaderPerspective()) {
		case multiFeature:
			extractor = new MultiFeatureExtractorInputReader();
			setConfig(urls.get(0), extractor, readerDto);
			break;
		default:
			throw new RuntimeException("[read] Not implemented:"
					+ readerDto.getReaderPerspective());
		}
		
		if(processedFrameLinstener != null && signalReader instanceof ProcessedFrameLinstener){
			((ProcessedFrameLinstener)signalReader).registerProcessedFrameLinstener(processedFrameLinstener);
		}
		log.debug("[getReader] working with extractor: " + extractor);
		signalReader.readSignal(urls, extractor);
		return extractor;
	}
	
	protected SignalReader createSignalReader(List<URL> url, WorkReadersEnum readerType){
		SignalReader signalReader = WorkAudioFactory.createAudioReader(url.get(0), readerType);
		return signalReader;
	}
	protected SignalReader createSignalReader(List<URL> url){
		SignalReader signalReader = WorkAudioFactory.createAudioReader(url.get(0), WorkReadersEnum.multiFeature);
		return signalReader;
	}
	
	public SignalFormat getSignalFormat(URL url) {
		SignalFormat signalFormat = WorkAudioFactory.createAudioReader(url, WorkReadersEnum.multiFeature)
		.getFormat(url);
		return signalFormat;
	}

	
	protected void setConfig(URL url, IExtractorInputReader extractor,FeatureReader readerDto){
		SignalFormat signalFormat = WorkAudioFactory.createAudioReader(url, readerDto.getReaderPerspective())
		.getFormat(url);
//		sampleRate = sampleRate == null?1:sampleRate;
		IExtractorConfig config = WorkUIExtractorConfigUtil.convert(readerDto.getWorkConfig(), signalFormat.getSampleRate());
		config.getExtractors().addAll(readerDto.getExtractors());
		config.getParameters().putAll(readerDto.getParameters());
		extractor.setConfig(config);

	}

	

}
