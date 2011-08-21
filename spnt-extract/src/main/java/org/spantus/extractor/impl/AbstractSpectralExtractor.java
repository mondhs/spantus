/*
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
package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.extractor.ExtractorsFactory;

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
public abstract class AbstractSpectralExtractor extends AbstractExtractor {
	
	private AbstractExtractorVector abstractExtractorVector;
	

	protected FrameVectorValues calculateFFT(FrameValues window){
		syncFFTParams();
		
		return getAbstractExtractorVector().calculateWindow(window);
	}
	
	private void syncFFTParams(){
		getAbstractExtractorVector().setConfig(getConfig());
	}
	
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues rtnValues = new FrameValues();
		return rtnValues;
	}
	
	
	public int getDimension() {
		return 1;
	}
	
	
	public Double getExtractorSampleRate() {
//		return (getConfig().getSampleRate()/(getWinowSize()*.85f));
		return super.getExtractorSampleRate();//(getConfig().getSampleRate()/(getConfig().getWindowSize()));

	}
	public void setAbstractExtractorVector(
			AbstractExtractorVector abstractExtractorVector) {
		this.abstractExtractorVector = abstractExtractorVector;
	}

	public AbstractExtractorVector getAbstractExtractorVector() {
		if(abstractExtractorVector == null){
			abstractExtractorVector = ExtractorsFactory.createFftExtractor();
		}
		return abstractExtractorVector;
	}

    @Override
    public void flush() {
        super.flush();
        getAbstractExtractorVector().flush();
    }

        
	

}
