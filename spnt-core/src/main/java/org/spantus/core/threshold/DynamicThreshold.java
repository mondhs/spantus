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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.Histogram.histogramEnum;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class DynamicThreshold extends StaticThreshold {
	
	private Float bufferedSampleSize;
	private Integer numberOfBins;
	private LinkedList<Float> firstBin = null;
	private Map<histogramEnum, Float> map = null;
	private Float prev = null;
	
	
	public DynamicThreshold() {
	}
	
	@Override
	public void afterCalculated(Long sample, FrameValues result) {
		recacluclateCurrentThreashold(result);
		super.afterCalculated(sample, result);

	}

//	@Override
//	protected Float calculateState(Long sample, Float windowValue,
//			Float threshold) {
//		Float calcThreshold = (prev-getCurrentThresholdValue());
//		calcThreshold = calcThreshold.isInfinite()?0F:calcThreshold;
//		return Math.abs(calcThreshold)>7000?1F:0F;//super.calculateState(sample, windowValue, threshold);
//	}
	
//	@Override
//	public Float calculateThreshold(Float windowValue) {
//		Float threshold = (prev-getCurrentThresholdValue());
//		threshold = threshold.isInfinite()?0F:threshold;
//		return Math.abs(threshold);
//		//super.calculateThreshold(windowValue);
//	}
	
	
	protected void recacluclateCurrentThreashold(FrameValues result){
		
		if(bufferedSampleSize == null){
			bufferedSampleSize = getExtractorSampleRate()*.3f;
			numberOfBins = log2(bufferedSampleSize.intValue()+1)+10;

		}
		
//		firstBin.addAll(result);
		
		if(map == null){
			map = Histogram.getMinAndMax(result);
		}else{
			map = Histogram.getMinAndMax(result,map);
		}
		List<List<Float>> histogram = Histogram.calculateHistogram(result,map,numberOfBins);
		
		getFirstBin().addAll(histogram.get(0));
		
		Float f = Histogram.calculateAvg(getFirstBin());

		int i = getFirstBin().size()-bufferedSampleSize.intValue();
		while( i > 0 ){
			getFirstBin().poll();
			i--;
		}
		
		if(f != null){
			prev = getCurrentThresholdValue();
			setCurrentThresholdValue(f);	
		}
		
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

	public LinkedList<Float> getFirstBin() {
		if(firstBin == null){
			firstBin = new LinkedList<Float>();
		}
		return firstBin;
	}

}
