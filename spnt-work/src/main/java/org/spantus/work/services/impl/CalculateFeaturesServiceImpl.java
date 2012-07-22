package org.spantus.work.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.DefaultExtractorInputReader;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.dao.ReaderDao;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.AudioUtil;
import org.spantus.core.io.ByteListInputStream;
import org.spantus.core.io.SignalReader;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.io.WorkAudioFactory;
import org.spantus.work.reader.MultiFeatureExtractorInputReader;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.services.WorkServiceFactory;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 *
 */
public class CalculateFeaturesServiceImpl{

	private ReaderDao readerDao;
	private Logger log = Logger.getLogger(CalculateFeaturesServiceImpl.class);

	public IExtractorInputReader calculateFeatures(URL wavFile){
		return calculateFeatures(wavFile, setupReaderByFile(wavFile));
	}
	public IExtractorInputReader calculateFeatures(URL signalFile, IExtractorConfig config){
		SignalReader workAudioReader = 
			WorkAudioFactory.createAudioReader(signalFile, WorkReadersEnum.multiFeature);
		IExtractorInputReader extractor = new MultiFeatureExtractorInputReader();
		extractor.setConfig(config);
		workAudioReader.readSignal(signalFile, extractor);
		return extractor;
	}
	
	public void saveData(IExtractorInputReader extractor, File featureFile){
		Assert.isTrue(extractor!=null);
		getReaderDao().write(extractor, featureFile);
		log.debug("Feature file saved to " + featureFile.toString());
	}
	
	/**
	 * Merge streams and calculate features
	 */
	public FrameValues merge(List<URL> urls){
		log.debug("[merge]merging [{0}]",urls);
//		List<URL> urls = new ArrayList<URL>(2);
//		urls.add(mainSignal);
//		urls.add(noiseSignal);
		AudioReader merger = AudioReaderFactory.createAudioReader();
		DefaultExtractorInputReader bufferedReader = new DefaultExtractorInputReader();
		ExtractorUtils.register(bufferedReader, new ExtractorEnum[]{
				ExtractorEnum.ENERGY_EXTRACTOR,
		}, null);
		
		bufferedReader.setConfig(setupReaderByFile(urls.get(0)));
		merger.readSignal(urls, bufferedReader);
		return bufferedReader.getFrameValues();
	}
	
	/**
	 * Save processed data
	 */
	public void saveMergedWav(List<URL> urls, File saveFile){
		AudioReader merger = AudioReaderFactory.createAudioReader();
		AudioFileFormat fileFormat = merger.findAudioFormat(urls.get(0));
		int sizeInBits = fileFormat.getFormat().getSampleSizeInBits();
		FrameValues mergeValues = merge(urls);
		List<Byte> mergedBuffer = new ArrayList<Byte>();

		for (Double float1 : mergeValues) {
			Byte[] bs = null;
			switch (sizeInBits) {
			   case 8:
				   bs = AudioUtil.get8(float1);
				   for (Byte byte1 : bs) {
					   mergedBuffer.add(byte1);
				   }
				   break;
			   case 16:
				   bs = AudioUtil.get16(float1, fileFormat.getFormat().isBigEndian());
				   for (Byte byte1 : bs) {
					   mergedBuffer.add(byte1);
				   }
				   break;
			}
		}
		InputStream bais = new ByteListInputStream(mergedBuffer);
		AudioInputStream ais = new AudioInputStream(bais, fileFormat.getFormat(), mergedBuffer.size());
		try {
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, saveFile);
		} catch (IOException e) {
			log.error(e);
			throw new ProcessingException(e);
		}
//		URL savedFile = ((MergedWraperExtractorReader)merger.getWraperExtractorReader())
//			.saveMerged(saveFile, merger.getWraperExtractorReader().getFormat());
		

		log.debug("Merged file saved to " + saveFile.toString());
	}
	
	/**
	 * setup configuration
	 * 
	 * @param bufferedReader
	 */
	public IExtractorConfig setupReaderByFile(URL wavFile){
		try {
			AudioFormat audioFormat;
			audioFormat = AudioSystem.getAudioFileFormat(wavFile).getFormat();
			IExtractorConfig config = ExtractorConfigUtil.defaultConfig(audioFormat);
			config.getExtractors().add(
					constructExtractorKey(SupportableReaderEnum.spantus, ExtractorEnum.ENERGY_EXTRACTOR));
			return config;
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	public String constructExtractorKey(Enum<?> supportable, Enum<?> extractor){
		return supportable.name() + ":" + extractor.name();
	}
	public ReaderDao getReaderDao() {
		if(readerDao == null){
			readerDao = WorkServiceFactory.createReaderDao();
		}
		return readerDao;
	}

}
