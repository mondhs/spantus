package org.spantus.android.segmentor.services.impl;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorConfig;

public final class AndroidExtractorConfigUtil {
	private AndroidExtractorConfigUtil() {
	}

	public static IExtractorConfig defaultConfig(Double sampleRate) {
		return defaultConfig(sampleRate, 33, 66);
	}

	public static IExtractorConfig defaultConfig(Double sampleRate,
			int windowLengthInMilSec, int overlapInPerc) {
		ExtractorConfig config = new ExtractorConfig();
		config.setSampleRate(sampleRate);
		Double windowSizeDouble = sampleRate * windowLengthInMilSec / 1000;
		windowSizeDouble = Math.max(1, windowSizeDouble);
		Integer windowSize = windowSizeDouble.intValue();
		config.setWindowSize(windowSize);

		float windowOverlapPercent = ((float) overlapInPerc) / 100;
		Double windowOverlapDouble = windowSizeDouble
				- (windowSizeDouble * windowOverlapPercent);
		Integer windowOverlap = windowOverlapDouble.intValue();
		windowOverlap = Math.max(1, windowOverlap);
		config.setWindowOverlap(windowOverlap.intValue());

		// enhancing as buffering is not working corretly
		config.setFrameSize(calculateFrameSize(windowSize, windowOverlap, 100));
		// config.setBitsPerSample(sampleSizeInBits);
		config.setBufferSize(calculateBufferSize(config.getFrameSize(), 80));
		return config;
	}

	public static int calculateFrameSize(int windowSize, int windowOverlap,
			int sizeInWindows) {
		int frameSize = windowOverlap * sizeInWindows
				+ (windowSize - windowOverlap);
		return frameSize;
	}

	public static int calculateBufferSize(int frameLength, int sizeInFrames) {
		int frameSize = frameLength * sizeInFrames;
		return frameSize;
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
		cloned.setWindowing(config.getWindowing());
		cloned.setSampleRate(config.getSampleRate());
		cloned.setPreemphasis(config.getPreemphasis());
		return cloned;
	}
}
