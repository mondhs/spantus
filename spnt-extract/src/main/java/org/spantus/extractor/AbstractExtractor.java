/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
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
public abstract class AbstractExtractor implements IExtractor {
	private Logger log = Logger.getLogger(AbstractExtractor.class);

	
	private ExtractorParam param = new ExtractorParam();

	private IExtractorConfig conf;
	
	private FrameValues windowValues;
	
	
	public IExtractorConfig getConfig() {
		return conf;
	}
	
	Windowing windowing;
	
	public void setConfig(IExtractorConfig conf) {
		this.conf = conf;
	}

	public ExtractorParam getParam() {
		return param;
	}

	public void putValues(Long sample, FrameValues values) {
		log.debug("[putValues]" + values.size());
	}
	public void setParam(ExtractorParam param) {
		this.param = param;
	}
	
	public int getWinowSize() {
		return getConfig().getWindowSize();
	}
	
	public FrameValues calculate(Long sampleNum, FrameValues values) {
//		log.debug("[calculate]+++  name:{0}; sampleRate:{1}; windowSize:{2}",
//				getName(), getConfig().getSampleRate()/1000, getConfig()
//						.getWindowSize());
		FrameValues calculatedValues = new FrameValues();
		int windowsIndex = 0; 
		int i = -1;
		for (Float f1: values) {
			i++;
			if(getWindowValues().size()<getConfig().getWindowOverlap()){
				//fill the window while it is empty
				getWindowValues().add(f1);
				continue;
			}
			if(windowsIndex == 0){
				FrameValues windowedWindow = new FrameValues(getWindowValues());
				getWindowing().apply(windowedWindow);
				//Calculating features values for the window
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

	protected FrameValues calculateWindow(FrameValues windowedWindow, FrameValues realValues){
		return calculateWindow(windowedWindow);
	}
	
	public int getDimension() {
		return 1;
	}

	
	public FrameValues getOutputValues() {
		throw new RuntimeException("Should be never called");
	}
	protected FrameValues getWindowValues() {
		if(windowValues == null){
			windowValues = new  FrameValues();
		}
		return windowValues;
	}
	
	public float getExtractorSampleRate() {
//		int overlap = (getConfig().getWindowSize() - getConfig().getWindowOverlap());
//		float calibratePrecent = (1-(overlap/getConfig().getWindowSize()));
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
		//do nothing
	}

}
