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
package org.spantus.core.threshold;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.Histogram.histogramEnum;
/**
 * {@link StaticThreshold}
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
//	private Float prev = null;
	private Float frameThreshold;
	
	/**
	 * recalculate threshold for each frame
	 */
	@Override
	public void afterCalculated(Long sample, FrameValues result) {
		frameThreshold = recacluclateCurrentThreashold(result);
		super.afterCalculated(sample, result);
	}
	
	/**
	 * return the value of current frame threshold. also it should set currentThresholdValue property.
	 * {@link StaticThreshold#calculateThreshold(Float)}
	 * 
	 */
	@Override
	public Float calculateThreshold(Float value) {
		setCurrentThresholdValue(frameThreshold);
		return frameThreshold;
	}
	/**
	 * Not use training functionality.
	 */
	@Override
	protected boolean isTrained(){
		return true;
	}

	
	protected Float recacluclateCurrentThreashold(FrameValues frameValues){
		
		if(bufferedSampleSize == null){
			bufferedSampleSize = getExtractorSampleRate()*.3f;
			numberOfBins = log2(bufferedSampleSize.intValue()+1)+10;

		}
		//calculate min max for frame values
		if(map == null){
			map = Histogram.getMinAndMax(frameValues);
		}else{
			map = Histogram.getMinAndMax(frameValues,map);
		}
		//calculate histogram of all frame values
		List<List<Float>> histogram = Histogram.calculateHistogram(frameValues,map,numberOfBins);
		
		//extract first not empty bin
		getFirstBin().addAll(findFirstBin(histogram));
		//calculate averages
		Float rtnThreshold = Histogram.calculateAvg(getFirstBin());

		int i = getFirstBin().size()-bufferedSampleSize.intValue();
		while( i > 0 ){
			getFirstBin().poll();
			i--;
		}
		
//		if(getCoef()>1){
			rtnThreshold +=Math.abs(rtnThreshold* (getCoef()));
//		}else{
//			rtnThreshold *= getCoef();
//		}
		return rtnThreshold;
		
	}
	/**
	 * fint first not empty bin
	 * @param histogram
	 * @return
	 */
	protected List<Float> findFirstBin(List<List<Float>> histogram){
		for (List<Float> list : histogram) {
			if(list.size()>0){
				return list;
			}
		}
		return Collections.emptyList();
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
	public Float getFrameThreshold() {
		return frameThreshold;
	}

	public void setFrameThreshold(Float frameThreshold) {
		this.frameThreshold = frameThreshold;
	}

	public Integer getNumberOfBins() {
		return numberOfBins;
	}

	public void setNumberOfBins(Integer numberOfBins) {
		this.numberOfBins = numberOfBins;
	}

	public Map<histogramEnum, Float> getMap() {
		return map;
	}

	public void setMap(Map<histogramEnum, Float> map) {
		this.map = map;
	}

	public void setFirstBin(LinkedList<Float> firstBin) {
		this.firstBin = firstBin;
	}

}
