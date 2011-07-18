/**
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
package org.spantus.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.24
 * 
 * Some utils method for matrix and vectors 
 * 
 */
public class MatrixUtils {
	/** 
	 * Init List of Doubles. all elements are 0
	 * @param order
	 * @return
	 */
	public static List<Double> zeros(int order){
		List<Double> zeros = new ArrayList<Double>(order);
		for (int i = 0; i < order; i++) {
			zeros.add(0D);
		}
		return zeros;
	}
	
	public static List<Double> fill(int order, Double f){
		List<Double> filled = new ArrayList<Double>(order);
		for (int i = 0; i < order; i++) {
			filled.add(f);
		}
		return filled;
	}
	
	public static List<Double> generareVector(Double value, int order){
		List<Double> zeros = new ArrayList<Double>();
		for (int i = 0; i < order; i++) {
			zeros.add(value);
		}
		return zeros;
	}
	
	public static List<Double> reverseVector(List<Double> vector){
		Collections.reverse(vector);
		return vector;
	}
	/**
	 * 
	 * @param values
	 * @return
	 */
    public static StringBuilder toString(List<List<Double>> values){
    	StringBuilder sb = new StringBuilder();
    	for (List<Double> list : values) {
			for (Double float1 : list) {
				sb.append(float1).append(";");
			}
			sb.append("\n");
		}
        return sb;
    }

	public static StringBuilder toStringTranform(
			List<LinkedList<Double>> values) {
		StringBuilder sb = new StringBuilder();
		int size  =values.get(0).size();
		List<List<Double>> newList = new ArrayList<List<Double>>(size);
		for (int i = 0; i < size; i++) {
			newList.add(new ArrayList<Double>());
		}
    	for (List<Double> list : values) {
    		int index = 0;
			for (Double float1 : list) {
				newList.get(index++).add(float1);
			}
		}
        return toString(newList);
	}


}
