/*
 * Copyright (c) 2010 Mindaugas Greibus (spantus@gmail.com)
 * Part of program for analyze speech signal
 * http://spantus.sourceforge.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.math.windowing.Windowing;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.math.windowing.WindowingFactory;

/**
 * User: mondhs
 * Date: 10.12.12
 * Time: 16.39
 */
public abstract class AbstractGeneralExtractor implements IGeneralExtractor {
    private IExtractorConfig config;

    private Windowing windowing;

    private ExtractorParam param = new ExtractorParam();
	private WindowBufferProcessor windowBufferProcessor;
    
    public void putValues(Long sample, FrameValues values) {
        //do nothing
    }

	public void flush() {
		//do nothing
	}

	/**
	 * 
	 * @param sampleNum
	 * @param values
	 * @return
	 */
	public IValues calculate(Long sampleNum, FrameValues values) {

		FrameValues windowValues = new FrameValues();
		IValues storedValues = null;

		boolean finished = false;
		while(!finished){
			windowValues = getWindowBufferProcessor().calculate(sampleNum, values, getConfig(), windowValues);
			finished = windowValues == null;
			if(finished){
				break;
			}
			getWindowing().apply(windowValues);
			storedValues = calculateAndStoreWindow(windowValues, storedValues);
		}
		storedValues.setSampleRate(getWindowBufferProcessor().calculateExtractorSampleRate(getConfig()));
		
		
//		log.debug("[calculate]---");
		return storedValues;
	}
	
	protected abstract IValues calculateAndStoreWindow(FrameValues windowedWindow, IValues storedValues);

    public void initParam(String key, String defaultValue) {
		for (String elemKey : getParam().getProperties().keySet()) {
			if (elemKey.equals(key)) {
				return;
			}
		}
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


    public int getWinowSize() {
        return getConfig().getWindowSize();
    }

    public IExtractorConfig getConfig() {
        return config;
    }


    public void setConfig(IExtractorConfig conf) {
        this.config = conf;
    }

    public Windowing getWindowing() {
        if (windowing == null) {
            WindowingEnum wenum = WindowingEnum.Hamming;
            if (getConfig().getWindowing() != null) {
                wenum = WindowingEnum.valueOf(getConfig().getWindowing());
            }
            windowing = WindowingFactory.createWindowing(wenum);
        }
        return windowing;
    }


    public ExtractorParam getParam() {
        return param;
    }

    public void setParam(ExtractorParam param) {
        this.param = param;
    }
	public WindowBufferProcessor getWindowBufferProcessor() {
		if(windowBufferProcessor == null){
			windowBufferProcessor = new WindowBufferProcessor();
		}
		return windowBufferProcessor;
	}
}
