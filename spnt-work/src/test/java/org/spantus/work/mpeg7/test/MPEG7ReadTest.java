package org.spantus.work.mpeg7.test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReader;
import org.spantus.logger.Logger;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.config.Mpeg7ExtractorConfig;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Factory;

public class MPEG7ReadTest extends TestCase {
	
	Logger log = Logger.getLogger(getClass());
	
	static Map<Mpeg7ExtractorEnum, Integer> extrSizes = new HashMap<Mpeg7ExtractorEnum, Integer>();
	static{
//		extrSizes.put(Mpeg7ExtractorEnum.HarmonicSpectralCentroid,2);
//		extrSizes.put(Mpeg7ExtractorEnum.HarmonicSpectralSpread,2);
		
//		extrSizes.put(Mpeg7ExtractorEnum.LogAttackTime, 0);
//		extrSizes.put(Mpeg7ExtractorEnum.TemporalCentroid, 0);
//		extrSizes.put(Mpeg7ExtractorEnum.SpectralCentroid, 0);
	}
	
	
	public void testReadEncode() throws MalformedURLException{
		AudioReader mpeg7 = Mpeg7Factory.createAudioReader();
		IExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		mpeg7.readSignal((new File("../data/text1.encode.xml")).toURI().toURL(), reader);
		assertEquals(3, reader.getExtractorRegister().size());
		assertEquals(3, reader.getExtractorRegister3D().size());
	}
	public void testReadService() throws MalformedURLException{
		AudioReader mpeg7 = Mpeg7Factory.createAudioReader();
		IExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		mpeg7.readSignal((new File("../data/text1.service.xml")).toURI().toURL(), reader);
		assertEquals(13, reader.getExtractorRegister().size());
		assertEquals(5, reader.getExtractorRegister3D().size());
	}
	public void testReadAudio() throws MalformedURLException{
		AudioReader mpeg7 = Mpeg7Factory.createAudioReader();
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		Mpeg7ExtractorConfig config = new Mpeg7ExtractorConfig();
		Set<Mpeg7ExtractorEnum> extractors = new LinkedHashSet<Mpeg7ExtractorEnum>();
		extractors.add(Mpeg7ExtractorEnum.AudioPower);
		extractors.add(Mpeg7ExtractorEnum.AudioWaveform);
		config.setExtractors(extractors);
		reader.setConfig(config);
		mpeg7.readSignal((new File("../data/text1.wav")).toURI().toURL(), reader);
		assertEquals(1, reader.getExtractorRegister().size());
		assertEquals(1, reader.getExtractorRegister3D().size());
	}
	public void testReadMpeg7Features() throws MalformedURLException{
		AudioReader mpeg7 = Mpeg7Factory.createAudioReader();
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		Mpeg7ExtractorConfig config = new Mpeg7ExtractorConfig();
		for (Mpeg7ExtractorEnum extr : Mpeg7ExtractorEnum.values()) {
			Set<Mpeg7ExtractorEnum> extractors = new LinkedHashSet<Mpeg7ExtractorEnum>();
			extractors.add(extr);
			config.setExtractors(extractors);
			reader.setConfig(config);
			reader.getExtractorRegister().clear();
			reader.getExtractorRegister3D().clear();
			mpeg7.readSignal((new File("../data/text1.wav")).toURI().toURL(), reader);
			if(reader.getExtractorRegister().size()+reader.getExtractorRegister3D().size() == 2){
				log.debug("extr: " + extr);
			}
			assertEquals("problem: " + extr,getExtractorTypeSize(extr), 
					reader.getExtractorRegister().size()+reader.getExtractorRegister3D().size());
		}
	}
	
	
	private int getExtractorTypeSize(Mpeg7ExtractorEnum extr){
		if(!extrSizes.containsKey(extr)){
			return 1;
		}else{
			return extrSizes.get(extr);
		}
	}

}
