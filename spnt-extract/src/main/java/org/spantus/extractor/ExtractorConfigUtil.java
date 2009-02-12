package org.spantus.extractor;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorConfig;

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
		return defaultConfig(sampleRate,sampleSizeInBits, .01f, 10);
	}
	public static IExtractorConfig  defaultConfig(float sampleRate, 
			int sampleSizeInBits, float windowLengthInSec, int overlapInPerc ){
		ExtractorConfig config = new ExtractorConfig();
		config.setSampleRate(sampleRate);
		Float windowSize = sampleRate * windowLengthInSec;
		config.setWindowSize(windowSize.intValue());
		Float windowOverlap = windowSize - (windowSize / overlapInPerc);
		config.setWindowOverlap(windowOverlap.intValue());
		Float frameSize = (windowSize * 10)+windowOverlap;
		config.setFrameSize(frameSize.intValue());
		config.setBitsPerSample(sampleSizeInBits);
		config.setBufferSize(config.getFrameSize()*10);
		return config;
	}
}
