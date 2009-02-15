
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
package org.spantus.work.ui.util;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.26
 *
 */
public abstract class WorkUIExtractorConfigUtil {
	public static IExtractorConfig convert(WorkUIExtractorConfig workConfig, float sampleRate){
		IExtractorConfig config = new ExtractorConfig();
		config.setSampleRate(sampleRate);
		float windowSize = (((float)sampleRate*workConfig.getWindowSize())/1000);
		config.setWindowSize((int)windowSize);
		float windowOverlapPercent = ((float)workConfig.getWindowOverlap())/100;
		float windowOverlap = windowSize - (windowSize * windowOverlapPercent); 

		config.setWindowOverlap((int)windowOverlap);

		config.setFrameSize((config.getWindowSize() * workConfig.getFrameSize())+config.getWindowOverlap());
		float bufferSize = (sampleRate * workConfig.getBufferSize())/windowSize;
		config.setBufferSize((int)bufferSize);
		return config;
	}
	
	public static IExtractorConfig convert(WorkUIExtractorConfig workConfig, int sampleRate){
		return convert(workConfig, (float)sampleRate);
	}

}
