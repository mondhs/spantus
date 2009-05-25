package org.spantus.work.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.MergeMultipleAudioReader;
import org.spantus.exception.ProcessingException;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.ConfigDao;
import org.spantus.work.services.ConfigPropertiesDao;

public class MergeNoise {
	
	public static final String FILE_NAME = "./config.properties";
	
	private URL mainSignal;
	private URL noiseSignal;
	private File saveWavFile;
	private File saveFeatureFile;
	private CalculateFeaturesServiceImpl calculateFeaturesService;
	ConfigDao configDao = new ConfigPropertiesDao();
	
	
	public void merge(){
		MergeMultipleAudioReader merger = getCalculateFeaturesService().merge(mainSignal, noiseSignal);
		getCalculateFeaturesService().saveMergedWav(merger, saveWavFile);
		AudioFormat audioFormat;
		try {
			try {
				audioFormat = AudioSystem.getAudioFileFormat(saveWavFile).getFormat();
			} catch (UnsupportedAudioFileException e) {
				throw new ProcessingException(e);
			} catch (IOException e) {
				throw new ProcessingException(e);
			}
			IExtractorConfig config = configDao.read(new File(FILE_NAME), audioFormat);
//			config.getExtractors().add(getCalculateFeaturesService()
//					.constructExtractorKey(SupportableReaderEnum.spantus, ExtractorEnum.AUTOCORRELATION_EXTRACTOR));
			IExtractorInputReader extractor = getCalculateFeaturesService().calculateFeatures(
					saveWavFile.toURI().toURL(),
					config);
			getCalculateFeaturesService().saveData(extractor, saveFeatureFile);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
	}
	

	/// setters and getters
	
	public void setMainSignal(URL mainSignal) {
		this.mainSignal = mainSignal;
	}

	public void setNoiseSignal(URL noiseSignal) {
		this.noiseSignal = noiseSignal;
	}
	public void setSaveWavFile(File saveFile) {
		this.saveWavFile = saveFile;
	}
	
	public File getSaveFeatureFile() {
		return saveFeatureFile;
	}

	public void setSaveFeatureFile(File featureFile) {
		this.saveFeatureFile = featureFile;
	}
	
	///main app func

	protected static void printUsage(){
		String usage = "2 parameters required";
		System.out.println(usage);
	}
	protected static void debug(String msg){
		System.out.println(msg);
	}
	protected static void error(String msg){
		System.err.println(msg);
	}
	public CalculateFeaturesServiceImpl getCalculateFeaturesService() {
		if(calculateFeaturesService == null){
			calculateFeaturesService = new CalculateFeaturesServiceImpl();
		}
		return calculateFeaturesService;
	}

	
	public static void main(String[] args) {
		if(args.length != 2){
			printUsage();
			return ;
		}
		
		File mainSignalFile = new File(args[0]);
		File noiseSignalFile = new File(args[1]);
		if(!mainSignalFile.exists()){
			error("Signal file not exists " + mainSignalFile.getAbsolutePath());
			return;
		}
		if(!noiseSignalFile.exists()){
			error("Noise file not exists " + noiseSignalFile.getAbsolutePath());
			return;
		}
		
		URL mainSignal;
		URL noiseSignal;
		try {
			mainSignal = mainSignalFile.toURI().toURL();
			noiseSignal = noiseSignalFile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		String savedFileName = FileUtils.getOnlyFileName(noiseSignalFile)+
		"_" + FileUtils.getOnlyFileName(mainSignalFile);
		File saveWavFile = new File(mainSignalFile.getParent(),
				savedFileName+".wav");
		if(saveWavFile.exists()){
			savedFileName+="-"+System.currentTimeMillis();
			saveWavFile = new File(mainSignalFile.getParent(),
					savedFileName+".wav");
		}
		MergeNoise mergeNoise = new MergeNoise();
		mergeNoise.setMainSignal(mainSignal);
		mergeNoise.setNoiseSignal(noiseSignal);
		mergeNoise.setSaveWavFile(saveWavFile);
		mergeNoise.setSaveFeatureFile(new File(mainSignalFile.getParent(),
				savedFileName+".sspnt.xml"));
		mergeNoise.merge();
	
	}

	

	
}
