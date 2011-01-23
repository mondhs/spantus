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

import java.util.LinkedList;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.extractor.modifiers.WienerModifier;
import org.spantus.logger.Logger;
import org.spantus.math.MatrixUtils;
import org.spantus.math.services.FFTService;
import org.spantus.math.services.MathServicesFactory;

/**
 * 
 * Fast Fourier transform feature
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class FFTExtractor extends AbstractExtractorVector {
	static Logger log = Logger.getLogger(FFTExtractor.class);

	private Integer upperFrequency;

	private FFTService fftService;

    private WienerModifier wienerFilter;

   public  FFTExtractor(){
            wienerFilter = new WienerModifier();
    }

    public String getName() {
		return ExtractorEnum.FFT_EXTRACTOR.toString();

	}
	
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameValues calculatedTempValues = window;
		FrameVectorValues calculatedValues = new FrameVectorValues();
		
//		float sampleRate = getConfig().getSampleRate();
		
		
		List<Float> fftOutput = getFftService().calculateFFTMagnitude(
                window.getFrameIndex(),
                calculatedTempValues, window.getSampleRate());
//		int upperLimit = (int)(getUpperFrequency()*fftOutput.size()/sampleRate);
//		upperLimit = Math.min(upperLimit, fftOutput.size());
//		fftOutput = fftOutput.subList(0, upperLimit);
        if(getWienerFilter()!=null){
            calculatedValues.add(getWienerFilter().calculateWindow(fftOutput, window));
        }else{
            calculatedValues.add(fftOutput);
        }

		return  calculatedValues;
	}
        
        
        





        
	private LinkedList<Float> buffer;
	LinkedList<Float> getBuffer(int order){
		if(buffer == null){
			buffer = new LinkedList<Float>();
			buffer.addAll(MatrixUtils.zeros(order));
		}
		return buffer;
	}


	public void setUpperFrequency(Integer upperFrequency) {
		this.upperFrequency = upperFrequency;
	}


	public Integer getUpperFrequency(){
		if(upperFrequency == null){
			return 6600;
		}
		return upperFrequency;
	}
    public WienerModifier getWienerFilter() {
        return wienerFilter;
    }

    public void setWienerFilter(WienerModifier wienerFilter) {
        this.wienerFilter = wienerFilter;
    }

    public FFTService getFftService() {
        if(fftService == null){
            fftService = MathServicesFactory.createFFTService();
        }
        return fftService;
    }

    public void setFftService(FFTService fftService) {
        this.fftService = fftService;
    }



}
