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
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 3, 2009
 *
 */
public class ExtractorResultBuffer3D implements IExtractorVector {
	
	Logger log = Logger.getLogger(ExtractorResultBuffer3D.class);
	
	IExtractorVector extractor;
	
	FrameValues frameValues = new FrameValues();
	FrameVectorValues outputValues = new FrameVectorValues();
	IExtractorConfig config;

	public ExtractorResultBuffer3D(IExtractorVector extractor) {
		this.extractor = extractor;
	}

	
	public void putValues(Long sample, FrameValues values) {
		this.frameValues = values;
		calculate(sample, values);
	}

	public FrameValues getFrameValues() {
		return frameValues;
	}
	
	public FrameVectorValues getOutputValues() {
		outputValues.setSampleRate(extractor.getExtractorSampleRate());
		return outputValues;
	}
	public void setOutputValues(FrameVectorValues outputValues) {
		this.outputValues = outputValues;
	}

	public ExtractorParam getParam() {
		return null;
	}

	public void setParam(ExtractorParam param) {

	}

	public String getName() {
		return extractor.getName();
	}

//	public int getWinowSize() {
//		return extractor.getConfig().getWindowSize();
//	}

	public FrameVectorValues calculate(Long sample, FrameValues values) {
		FrameVectorValues outputValues = extractor.calculate(sample, getFrameValues());
		
		getOutputValues().addAll(outputValues);
		int i = getOutputValues().size() - getConfig().getBufferSize();
		while( i > 0 ){
			getOutputValues().poll();
			i--;
		}
                outputValues.setSampleRate(getExtractorSampleRate());
		return outputValues;
	}



	
	public IExtractorConfig getConfig() {
		return extractor.getConfig();
	}

	
	public void setConfig(IExtractorConfig config) {
		extractor.setConfig(config);
	}


	
	public FrameVectorValues calculateWindow(FrameValues window) {
		return null;
	}


	
	public Double getExtractorSampleRate() {
		return getConfig().getSampleRate();
	}
	public void flush() {
		extractor.flush();		
	}
}
