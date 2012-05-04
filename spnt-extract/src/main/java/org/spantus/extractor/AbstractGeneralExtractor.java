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
import org.spantus.core.IValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.extractor.windowing.WindowBufferProcessor;

/**
 * User: mondhs
 * Date: 10.12.12
 * Time: 16.39
 */
public abstract class AbstractGeneralExtractor<T extends IValues> implements IGeneralExtractor<T> {
    private IExtractorConfig config;

    
    private ExtractorParam param = new ExtractorParam();
    
    private T outputValues;
    
    public AbstractGeneralExtractor() {
	}
    
    
	@Override
	public void flush() {
		
	}
	
	public T calculateWindow(Long sampleNum, FrameValues values) {
		T fv = calculateWindow(values);
		fv.setSampleRate(getExtractorSampleRate());
		return fv;
	}
	
	@Override
	public Double getExtractorSampleRate() {
		return WindowBufferProcessor.calculateExtractorSampleRateStatic(getConfig());
	}


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


    public void setConfig(IExtractorConfig config) {
        this.config = config;
    }




    public ExtractorParam getParam() {
        return param;
    }

    public void setParam(ExtractorParam param) {
        this.param = param;
    }

	/**
	 * 
	 * @param config
	 * @return
	 */
	public Double calculateExtractorSampleRate(IExtractorConfig config) {
		return (config.getSampleRate()/(config.getWindowSize()-config.getWindowOverlap()));
	}
    

	
	public long getOffset() {
		return 0;
	}

	public T getOutputValues() {
		return outputValues;
	}

	public void setOutputValues(T outputValues) {
		this.outputValues = outputValues;
	}
}
