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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Apr 8, 2009
 *
 */
public abstract class Histogram {
	
	public enum histogramEnum{min, max};
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static List<List<Float>> calculateHistogram(List<Float> list){
		//Sturges' formula, numbers of bin
		int numberOfBins = log2(list.size())+1;
		
		Map<histogramEnum, Float> map = getMinAndMax(list);
		
		return calculateHistogram(list, 
				map.get(histogramEnum.min), 
				map.get(histogramEnum.max),
				numberOfBins);
		
	}
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static Map<histogramEnum, Float> getMinAndMax(List<Float> list){
		Map<histogramEnum, Float> map = new HashMap<histogramEnum, Float>(2);
		
		Float min = Float.MAX_VALUE;
		Float max = -Float.MAX_VALUE;
		
		for (Float float1 : list) {
			min = Math.min(min, float1);
			max = Math.max(max, float1);
		}
		map.put(histogramEnum.min, min);
		map.put(histogramEnum.max, max);
		return map;
	}
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static Map<histogramEnum, Float> getMinAndMax(List<Float> list, Map<histogramEnum, Float> map){
		Map<histogramEnum, Float> curr = getMinAndMax(list);
		curr.put(histogramEnum.max, Math.max(map.get(histogramEnum.max), curr.get(histogramEnum.max)));
		curr.put(histogramEnum.min, Math.min(map.get(histogramEnum.min), curr.get(histogramEnum.min)));
		return curr;
	}
	/**
	 * 
	 * @param list
	 * @param map
	 * @param numberOfBins
	 * @return
	 */
	public static List<List<Float>> calculateHistogram(List<Float> list, Map<histogramEnum, Float> map, int numberOfBins){
		return calculateHistogram(list, 
				map.get(histogramEnum.min), 
				map.get(histogramEnum.max),
				numberOfBins);
	}
	
	/**
	 * 
	 * @param list
	 * @param min
	 * @param max
	 * @param numberOfBins
	 * @return
	 */
	public static List<List<Float>> calculateHistogram(List<Float> list, Float min, Float max, int numberOfBins){
		Float step = (max-min)/numberOfBins;
		List<List<Float>> histogram = new ArrayList<List<Float>>(numberOfBins+3);
		for (int i = 0; i < numberOfBins+3; i++) {
			histogram.add(new LinkedList<Float>());
		}
		
		for (Float float1 : list) {
			Float indexFloat  = (float1-min)/step;
			int index = indexFloat.intValue();
			safeAdd(histogram, index, float1);
		}
		return histogram;
	}
	/**
	 * 
	 * @param histogram
	 * @return
	 */
	public static Float calculateAvgForFirstBin(List<List<Float>> histogram){
		int histogramBin = new Float(histogram.size()*.05f).intValue();
		Float avg = null;
		for (Float float1 : histogram.get(histogramBin)) {
			avg = average(avg, float1);
		}
		return avg;
	}
	/**
	 * 
	 * @param histogram
	 * @return
	 */
	public static Float calculateAvg(List<Float> list){
		Float avg = null;
		for (Float float1 : list) {
			avg = average(avg, float1);
		}
		return avg;
	}
	/**
	 * 
	 * @param avg
	 * @param f1
	 * @return
	 */
	public static Float average(Float avg, Float f1){
		Float rtnAvg = avg;
		if(rtnAvg == null){ rtnAvg = f1;}
		rtnAvg = (rtnAvg+f1)/2;
		return rtnAvg;
	}
	/**
	 * 
	 * @param list
	 * @param index
	 * @param f
	 */
	public static void safeAdd(List<List<Float>> list, int index, Float f){
//		if(list.get(index)==null){
//			list.set(index, new Lin);
//		}
//		Assert.isTrue(index=>0, "index: " + index);
		list.get(index).add(f);
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
