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
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.06.10
 *
 */
public class NoiseLevelExtractor extends AbstractSpectralExtractor {

	private Double estimate = 0D;
	private Double noiseEstimate = 0D;
	private Double noiseThreshold = 0D;
	private Float previous;
	
	public NoiseLevelExtractor() {
		super.setAbstractExtractorVector(new SpectralGainFactorExtractor());
	}
	
	protected FrameValues calculateWindow(FrameValues windowedWindow, FrameValues realValues){
		return calculateWindow(realValues);
	}
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues rtnValues = super.calculateWindow(window);
		Float max = -Float.MAX_VALUE;
		for (Float value : window) {
			max = Math.max(max, estimate(value));
		}
		rtnValues.add(max);

		
//		float windowVal = 0f;
//		for (Float float1 : window) {
//			Float val = (float1 - max); 
//			val = (float)Math.pow(val, 2);
//			windowVal += val;
//		}
//		rtnValues.add(windowVal);
		return rtnValues;
	}

	protected Float estimate(Float value){
		previous = previous == null ? value : previous;
		Double BEstimateCoef = 
//			(getConfig().getSampleRate()* .989992)/8000F; 
		.9; 
//			1.0F;
		Double BNoiseCoef = 
//			(getConfig().getSampleRate()* .9922)/8000F;
			.9922;
//			.999999	;
		Double BThresholdCoef = 
//			(getConfig().getSampleRate()* .98975)/8000F;
			.98975;

		
		//emphaseValue  - u(k)
		Double emphaseValue = Math.abs(value- .95*previous);//Math.abs(value - .95F*previous);
		//estimate - s(k)
		if(estimate>emphaseValue){
			estimate = emphaseValue;			
		}else{
			estimate = (1-BEstimateCoef) * emphaseValue + BEstimateCoef * estimate;
		}
		
		
		//noiseEstimate - n(k)
		if(noiseEstimate>emphaseValue){
			noiseEstimate = emphaseValue;
		}else{
			noiseEstimate = (1-BNoiseCoef) * emphaseValue + BNoiseCoef * noiseEstimate;
		}
		
		
		//noiseThreshold - tn(k)
		if(noiseThreshold>noiseEstimate){
			noiseThreshold = (1-BThresholdCoef) * noiseEstimate + BThresholdCoef * noiseThreshold;
		}else{
			noiseThreshold = noiseEstimate;
		}
		return noiseThreshold.floatValue();
	}
	
	public String getName() {
		return ExtractorEnum.NOISE_LEVEL_EXTRACTOR.toString();
	}
	



}
