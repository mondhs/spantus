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
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 3, 2009
 *
 */
public class ExtractorResultBuffer implements IExtractor {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ExtractorResultBuffer.class);
	private IExtractor extractor;

	private FrameValues outputValues = new FrameValues();
	private long offset = 0;

	public ExtractorResultBuffer(IExtractor extractor) {
		this.extractor = extractor;
	}
	
	public void putValues(Long sample, FrameValues values) {
//		this.frameValues = values;
		calculate(sample, values);
	}

//	public FrameValues getFrameValues() {
//		return frameValues;
//	}
	
	public FrameValues getOutputValues() {
		outputValues.setSampleRate(extractor.getExtractorSampleRate());
		return outputValues;
	}
	public void setOutputValues(FrameValues outputValues) {
		this.outputValues = outputValues;
	}


	public String getName() {
		return extractor.getName();
	}

	public FrameValues calculateWindow(FrameValues window) {
		throw new RuntimeException("This method should not be called ever. You have to write your own implementation");
	}

	public int getWindowSize() {
		return extractor.getConfig().getWindowSize();
	}


	public FrameValues calculate(Long sample, FrameValues values) {
		FrameValues val = extractor.calculate(sample, values);
		if(val == null){
			return null;
		}
		getOutputValues().addAll(val);
		int i = getOutputValues().size() - getConfig().getBufferSize();
		while( i > 0 ){
			getOutputValues().poll();
			offset++;
			i--;
		}
                val.setSampleRate(getExtractorSampleRate());
		return val;
	}

	
	public IExtractorConfig getConfig() {
		return extractor.getConfig();
	}

	
	public void setConfig(IExtractorConfig config) {
		extractor.setConfig(config);
		
	}

	
	public Double getExtractorSampleRate() {
		return extractor.getExtractorSampleRate();
		
	}
	@Override
	public String toString() {
		return getClass().getSimpleName()+ ":" + getName();
	}
	
	public void flush() {
		extractor.flush();		
	}
	
	@Override
	public long getOffset() {
		return offset;
	}
}
