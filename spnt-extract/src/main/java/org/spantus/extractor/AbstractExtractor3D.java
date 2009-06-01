/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
public abstract class AbstractExtractor3D implements IExtractorVector {
	Logger log = Logger.getLogger(AbstractExtractor3D.class);
	
	private IExtractorConfig config;

	private ExtractorParam param = new ExtractorParam();

	private Windowing windowing;

	private FrameValues windowValues;
	
	public ExtractorParam getParam() {
		return param;
	}
	
	public void setParam(ExtractorParam param) {
		this.param = param;
	}
	public void putValues(Long sample, FrameValues values) {
		log.debug("[putValues]" +values.size());
	}
	public void initParam(String key, String defaultValue) {
		for (String elemKey : getParam().getProperties().keySet()) {
			if (elemKey.equals(key)) {
				return;
			}
		}
		// getParam().getProperties().put(key, defaultValue);
		// }
	}

	public int getParam(String key, int defaultValue) {
		Object o = getParam().getProperties().get(key);
		if(o != null && o instanceof Number){
			return ((Number)o).intValue();
		}
		return defaultValue;
	}

	public boolean getParam(String key, boolean defaultValue) {
		Object o = getParam().getProperties().get(key);
		if(o != null && o instanceof Number){
			return ((Boolean)o).booleanValue();
		}
		return defaultValue;
	}

	public FrameVectorValues calculate(Long sample, FrameValues values) {
//		log.debug(
//				"[calculate]+++  name:{0}; sampleRate:{1}; windowSize{2}",
//				getName(), getConfig().getSampleRate()/1000, getConfig()
//						.getWindowSize());
		FrameVectorValues calculatedValues = new FrameVectorValues();
		int windowsIndex = 0; 
		for (Float f1: values) {
			if(getWindowValues().size()<getConfig().getWindowOverlap()){
				//fill the window while it is empty
				getWindowValues().add(f1);
				continue;
			}
			if(windowsIndex == 0){
				FrameValues windowedWindow = new FrameValues(getWindowValues());
				getWindowing().apply(windowedWindow);
				calculatedValues.addAll(calculateWindow(windowedWindow, getWindowValues()));
				windowsIndex = getConfig().getWindowOverlap();
			}
			getWindowValues().add(f1);
			getWindowValues().poll();
			windowsIndex--;
		}
	
		
//		log.debug("[calculate]---");
		return calculatedValues;
	}

	protected FrameVectorValues calculateWindow(FrameValues windowedWindow, FrameValues realValues){
		return calculateWindow(windowedWindow);
	}
	
	public int getWinowSize() {
		return getConfig().getWindowSize();
	}
	
	public FrameVectorValues getOutputValues() {
		throw new RuntimeException("Should be never call");
	}
	
	public IExtractorConfig getConfig() {
		return this.config;
	}

	
	public void setConfig(IExtractorConfig config) {
		this.config = config;
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

	public Windowing getWindowing() {
		if(windowing == null){
			WindowingEnum wenum = WindowingEnum.Hamming;
			if(getConfig().getWindowing() != null){
				wenum = WindowingEnum.valueOf(getConfig().getWindowing());
			}
			windowing = WindowingFactory.createWindowing(wenum);
		}
		return windowing;
	}
	
	public void flush() {
		//doNothing
	}



}
