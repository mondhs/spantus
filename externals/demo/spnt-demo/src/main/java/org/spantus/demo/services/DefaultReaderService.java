/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.demo.services;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.demo.dto.ReaderDto;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.config.Mpeg7ConfigUtil;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Factory;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class DefaultReaderService implements ReaderService {

	
	public IExtractorInputReader getReader(URL url,
			ReaderDto readerDto) {
		IExtractorInputReader reader = null;
		if(url.getFile().endsWith("xml") && !ReadersEnum.mpeg7.equals(readerDto.getReader())){
			readerDto.setReader(ReadersEnum.mpeg7);
			readerDto.getExtractors().clear();
		}
		
		switch (readerDto.getReader()) {
		case common:
			reader = readCommon(url, readerDto);
			break;
		case mpeg7:
			reader = readMpeg7(url, readerDto);
			break;
		default:
			throw new RuntimeException("Not impl: " + readerDto.getReader());
		}
		return reader;
	}
	/**
	 * 
	 * @param url
	 * @param readerDto
	 * @return
	 */
	private IExtractorInputReader readMpeg7(URL url, ReaderDto readerDto){
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		Set<Mpeg7ExtractorEnum> extrEnum = new HashSet<Mpeg7ExtractorEnum>();
		for (String extr : readerDto.getExtractors()) {
			extrEnum.add(Mpeg7ExtractorEnum.valueOf(extr));
		}
		reader.setConfig(Mpeg7ConfigUtil.createConfig(extrEnum));
		Mpeg7Factory.createAudioReader().readAudio(url, reader);
		return reader;
	}
	/**
	 * 
	 * @param url
	 * @param readerDto
	 * @return
	 */
	private IExtractorInputReader readCommon(URL url, ReaderDto readerDto){
		AudioReader audioReader = AudioFactory.createAudioReader();
		
		IExtractorInputReader reader = ExtractorsFactory.createNormalizedReader();
		AudioFormat format = audioReader.getAudioFormat(url).getFormat();
		reader.setConfig(
			ExtractorConfigUtil.defaultConfig(format)
		);
		for (String demoExtractorEnum : readerDto.getExtractors()) {
			ExtractorUtils.register(reader, ExtractorEnum.valueOf(demoExtractorEnum), 
			null);
		}
		audioReader.readAudio(url, reader);
		return reader;
	}
	public IExtractorConfig getConfig(){
		return new ExtractorConfig();
	}

}
