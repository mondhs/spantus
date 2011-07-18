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
package org.spantus.extractor;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 3, 2009
 *
 */
public abstract class ExtractorConfigUtil {
	public static IExtractorConfig  defaultConfig(AudioFormat format){
		return defaultConfig((double)format.getSampleRate());
	}
	public static IExtractorConfig  defaultConfig(Double sampleRate){
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
		return defaultConfig(sampleRate, 33, 66);
	}
	public static IExtractorConfig  defaultConfig(Double sampleRate, 
			 int windowLengthInMilSec, int overlapInPerc ){
		ExtractorConfig config = new ExtractorConfig();
		config.setSampleRate(sampleRate);
		config.setBufferSize(3000);
		Double windowSize = sampleRate * windowLengthInMilSec / 1000;
		windowSize = Math.max(1, windowSize);
		config.setWindowSize(windowSize.intValue());
		float windowOverlapPercent = ((float)overlapInPerc)/100;
		
		Double windowOverlap = windowSize - (windowSize * windowOverlapPercent);
		windowOverlap = Math.max(1, windowOverlap);
		config.setWindowOverlap(windowOverlap.intValue());
		
		
		Double frameSize = (windowSize * 10)+windowOverlap;
		config.setFrameSize(frameSize.intValue());
//		config.setBitsPerSample(sampleSizeInBits);
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
		cloned.setWindowing(config.getWindowing());
		cloned.setSampleRate(config.getSampleRate());
		cloned.setPreemphasis(config.getPreemphasis());
		return cloned;
	}
}
