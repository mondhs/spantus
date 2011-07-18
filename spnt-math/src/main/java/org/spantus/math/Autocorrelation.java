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

import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.24
 *
 * Calculated autocorrelation of a signal
 */
public class Autocorrelation {
	/**
	 * 
	 * @param window - short time signal values 
	 * @param order - autocorrelation order, common use = 10
	 * @return list of autocorrelation values with size 'order' 
	 */
	public static List<Double> calc(List<Double> window, int order){
		//autocorrelation coefficients R
		List<Double> autocorr = MatrixUtils.zeros(order);
		for (int i = 0; i < order; i++) {
			double sum = 0d;
			for (int n = 0; n < window.size()-i; n++) {
				sum += window.get(n) * window.get(n+i);
			}
			autocorr.set(i, sum);
		}
		
		Double maxval = autocorr.get(0);
		for (int i = 0; i < autocorr.size(); i++) {
			autocorr.set(i, autocorr.get(i)/maxval);
		}
		
		return autocorr;
	}

}
