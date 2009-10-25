package org.spantus.work.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.MergeMultipleAudioReader;
import org.spantus.core.io.MergedWraperExtractorReader;
import org.spantus.core.io.SignalReader;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.io.WorkAudioFactory;
import org.spantus.work.reader.MultiFeatureExtractorInputReader;
import org.spantus.work.reader.SupportableReaderEnum;
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
	public MergeMultipleAudioReader merge(URL mainSignal, URL noiseSignal){
		log.debug("merging signal [{0}] and noise[{1}]",mainSignal.toString(),noiseSignal.toString());
		MergeMultipleAudioReader merger = (MergeMultipleAudioReader)AudioFactory.createAudioReader(noiseSignal);
		IExtractorInputReader bufferedReader = new ExtractorInputReader();
		ExtractorUtils.register(bufferedReader, new ExtractorEnum[]{
				ExtractorEnum.ENERGY_EXTRACTOR,
		}, null);
		
		bufferedReader.setConfig(setupReaderByFile(mainSignal));
		merger.readSignal(mainSignal, bufferedReader);
		return merger;
	}
	
	/**
	 * Save processed data
	 */
	public void saveMergedWav(MergeMultipleAudioReader merger, File saveFile){
		URL savedFile = ((MergedWraperExtractorReader)merger.getWraperExtractorReader())
			.saveMerged(saveFile, merger.getWraperExtractorReader().getFormat());
		log.debug("Merged file saved to " + savedFile.toString());
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
