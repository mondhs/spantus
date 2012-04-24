/**
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
package org.spantus.android.service;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorConfig;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Jun 3, 2009
 * 
 */
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
