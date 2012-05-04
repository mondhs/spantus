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
package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractorVector;

/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.18
 *
 */
public abstract class AbstractSpectralVectorExtractor extends AbstractExtractorVector {
	
	AbstractExtractorVector abstractExtractorVector;
	float signalSampleRate;
	
	protected FrameVectorValues calculateFFT(FrameValues window){
		syncFFTParams();
		
		return getAbstractExtractorVector().calculateWindow(window);
	}
	
	private void syncFFTParams(){
		getAbstractExtractorVector().setConfig(getConfig());
	}
	
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues rtnValues = new FrameVectorValues();
		rtnValues.setSampleRate(getExtractorSampleRate());
		return rtnValues;
	}
	
	
	public int getDimension() {
		return 1;
	}
	
	

	public AbstractExtractorVector getAbstractExtractorVector() {
		if(abstractExtractorVector == null){
			abstractExtractorVector = ExtractorUtils.createFftExtractor();
		}
		return abstractExtractorVector;
	}

	

}
