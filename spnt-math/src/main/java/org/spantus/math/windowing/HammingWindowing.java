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
package org.spantus.math.windowing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class HammingWindowing extends Windowing {

	Map<Integer, List<Double>> cache;
	
	public void apply(List<Double> values) {
//		int nSamples = values.size();
		List<Double> result = new ArrayList<Double>();
		Iterator<Double> windowIterator = getFromCache(values.size()).iterator();
		Iterator<Double> valuesIterator = values.iterator();
		while (valuesIterator.hasNext()) {
			Double val = valuesIterator.next();
			Double win = windowIterator.next();
			result.add(val*win);
		}
		values.clear();
		values.addAll(result);
	}
	/**
	 * 
	 * @param size
	 * @return
	 */
	protected List<Double> getFromCache(int size){
		if(cache == null){
			cache = new HashMap<Integer, List<Double>>();
		}
		if(cache.get(size)==null){
			cache.put(size, calculate(size));	
		}
		return cache.get(size);
	}
	/**
	 * 
	 * @param size
	 * @return
	 */
	public List<Double> calculate(int size){
		int j;
		List<Double> result = new ArrayList<Double>();
		for (j = 0-size/2 ; j < size/2; j++){
			Double d =(0.54 + 0.46 *  Math.cos(2.0 *  Math.PI * j / size));
				result.add(d);
		}
		if(size>result.size()){
			Double d =(0.54 + 0.46 *  Math.cos(2.0 * Math.PI * j / size));
			result.add(d);
		}
		return result;
	}
	
	public Double calculate(int size, int index){
		int j = index-(size/2);
		Double d =(0.54 + 0.46 * Math.cos(2.0 *  Math.PI * j / size));
		if(index==size){
			d =(0.54 + 0.46 *  Math.cos(2.0 * Math.PI * size / size));
		}
		return d;
	}
	
}
