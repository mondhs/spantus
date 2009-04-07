/*
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
package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class SampleEstimationThreshold extends StaticThreshold {

	@Override
	public void setExtractor(IExtractor extractor) {
		super.setExtractor(extractor);
//		afterCalculated(0L, extractor.getOutputValues());
	}
	@Override
	public void afterCalculated(Long sample, FrameValues result) {
		estimateThreshold(result);
		super.afterCalculated(sample, result);
		FrameValues newThresholdVals = new FrameValues();
		FrameValues  newState = new FrameValues();
		for (Float float1 : getOutputValues()) {
//		for (int i = 0; i < getThresholdValues().size(); i++) {
			Float threshold = getCurrentThresholdValue();
			newThresholdVals.add(threshold);
			newState.add(calculateState(sample, float1, threshold));
		}
		thereshold = newThresholdVals;
		state = newState;
	}

	public void estimateThreshold(FrameValues result){
		//Sturges' formula, numbers of bin
		int k = log2(result.size())+1;
		Float min = Float.MAX_VALUE;
		Float max = -Float.MAX_VALUE;

		for (Float float1 : result) {
			min = Math.min(min, float1);
			max = Math.max(max, float1);
		}
		Float step = (max-min)/k;
		Float[] histogram = new Float[k+2];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = Float.valueOf(0f);
			
		}
		
		Float histogramBin = k*.05f;
//		histogramBin = histogramBin < 1?1:histogramBin;
		Float avgThreshold = null, maxThreshold = -Float.MAX_VALUE;
		for (Float float1 : result) {
			Float i  = (float1-min)/step;
			if(histogramBin.intValue()==i.intValue()){
				if(avgThreshold == null){ avgThreshold = float1;}
				avgThreshold = (avgThreshold+float1)/2;
				maxThreshold = Math.max(maxThreshold, float1);
			}
			histogram[i.intValue()]++;
			
		}
		setCurrentThresholdValue(avgThreshold);
		
	}
	
	@Override
	protected boolean isTrained(){
		return true;
	}
	/**
	 * 
	 * @param d
	 * @return
	 */
	public static int log2(int d) {
		Double l = Math.log(d) / Math.log(2.0);
		return l.intValue();
	}

}
