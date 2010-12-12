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

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.logger.Logger;
import org.spantus.math.windowing.Windowing;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.math.windowing.WindowingFactory;


/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.01
 *
 */
public abstract class AbstractExtractorVector extends AbstractGeneralExtractor implements IExtractorVector {
	Logger log = Logger.getLogger(AbstractExtractorVector.class);
	

	private FrameValues windowValues;


	public FrameVectorValues calculate(Long sampleNum, FrameValues values) {
//		log.debug(
//				"[calculate]+++  name:{0}; sampleRate:{1}; windowSize{2}",
//				getName(), getConfig().getSampleRate()/1000, getConfig()
//						.getWindowSize());
		FrameVectorValues calculatedValues = new FrameVectorValues();
        long frameIndexStart = sampleNum - getConfig().getFrameSize();
        long frameIndex = 0L;
		int windowsIndex = 0; 
		for (Float f1: values) {
			if(getWindowValues().size()<getConfig().getWindowOverlap()){
				//fill the window while it is empty
				getWindowValues().add(f1);
				continue;
			}
			if(windowsIndex == 0){
				FrameValues windowedWindow = new FrameValues(getWindowValues());
                windowedWindow.setFrameIndex(frameIndexStart+1+frameIndex);
                frameIndex++;
				getWindowing().apply(windowedWindow);
                calculatedValues.addAll(calculateWindow(windowedWindow, getWindowValues()));
				windowsIndex = getConfig().getWindowOverlap();

			}
			getWindowValues().add(f1);
			getWindowValues().poll();
			windowsIndex--;
		}

        calculatedValues.setSampleRate(getExtractorSampleRate());

		
//		log.debug("[calculate]---");
		return calculatedValues;
	}


	protected FrameVectorValues calculateWindow(FrameValues windowedWindow, FrameValues realValues){
                FrameVectorValues fv = calculateWindow(windowedWindow);
                fv.setSampleRate(getExtractorSampleRate());
                return fv;
	}

	
	public FrameVectorValues getOutputValues() {
		throw new RuntimeException("Should be never call");
	}
	

	
	protected FrameValues getWindowValues() {
		if(windowValues == null){
			windowValues = new FrameValues();
		}
		return windowValues;
	}
	
	public float getExtractorSampleRate() {
		return (getConfig().getSampleRate()/(getConfig().getWindowOverlap()));
	}





}
