package org.spantus.extractor;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;

public abstract class ExtractorConfigUtil {
	public static IExtractorConfig  defaultConfig(AudioFormat format){
		return defaultConfig(format.getSampleRate(), format.getSampleSizeInBits());
	}
	public static IExtractorConfig  defaultConfig(float sampleRate, int sampleSizeInBits){
//		ExtractorConfig config = new ExtractorConfig();
//		config.setSampleRate(sampleRate);
//		Float windowSize = sampleRate*.01f;//10ms/1000
//		config.setWindowSize(windowSize.intValue());
//		Float windowOverlap = windowSize - (windowSize / 10);
//		config.setWindowOverlap(windowOverlap.intValue());
//		Float frameSize = (windowSize * 10)+windowOverlap;
//		config.setFrameSize(frameSize.intValue());
//		config.setBitsPerSample(sampleSizeInBits);
//		config.setBufferSize(bufferSize);
		return defaultConfig(sampleRate,sampleSizeInBits, 10, 10);
	}
	public static IExtractorConfig  defaultConfig(float sampleRate, 
			int sampleSizeInBits, int windowLengthInMilSec, int overlapInPerc ){
		ExtractorConfig config = new ExtractorConfig();
		config.setSampleRate(sampleRate);
		Float windowSize = sampleRate * windowLengthInMilSec / 1000;
		config.setWindowSize(windowSize.intValue());
		Float windowOverlap = windowSize - (windowSize / overlapInPerc);
		config.setWindowOverlap(windowOverlap.intValue());
		Float frameSize = (windowSize * 10)+windowOverlap;
		config.setFrameSize(frameSize.intValue());
		config.setBitsPerSample(sampleSizeInBits);
		config.setBufferSize(config.getFrameSize()*10);
		return config;
	}
	
	public static IExtractorConfig clone(IExtractorConfig config) {
		IExtractorConfig cloned = null;
		try {
			cloned = config.getClass().newInstance();
		} catch (InstantiationException e) {
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(e);
		}
		cloned.setFrameSize(config.getFrameSize());
		cloned.setBufferSize(config.getBufferSize());
		cloned.setWindowSize(config.getWindowSize());
		cloned.setWindowOverlap(config.getWindowOverlap());
		cloned.setSampleRate(config.getSampleRate());
		return cloned;
	}
}
