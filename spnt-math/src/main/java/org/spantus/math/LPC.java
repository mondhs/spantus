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
 * Created 2008.02.20
 * 
 * Thanks for algorithm implementation of Mark Huckvale University College London C++ 1996
 * http://www.phon.ucl.ac.uk/courses/spsci/dsp/lpc.html
 * 
 * Based on cmu sphinx: edu.cmu.sphinx.frontend.frequencywarp.LinearPredictor
 * 
 */
public class LPC {

	/**
	 * 
	 */
	public static List<Float> calcForAutocorr(List<Float> autocorr){
		int order = autocorr.size()-1;
		List<Float> lpc = MatrixUtils.zeros(order+1);//intialize all lpc coef to 0 //LPC coef
		List<Float> reflection = MatrixUtils.zeros(order+1);//Reflection coef. should be always < 1 to have stable system
		List<Float> backwardPredictor = MatrixUtils.zeros(order+1);

		float error = autocorr.get(0);
		reflection.set(1, -autocorr.get(1).floatValue()/autocorr.get(0).floatValue());
		lpc.set(0, new Float(1.0));//fist coef should be 1
		lpc.set(1, reflection.get(1).floatValue());
		error *= (1 - reflection.get(1).floatValue() * reflection.get(1).floatValue()); 
		
		for (int i = 2; i <= order; i++) {
			for (int j = 1; j < i; j++) {
				backwardPredictor.set(j, lpc.get(i - j).floatValue());
			}
			reflection.set(i, 0.0f);
			for (int j = 0; j < i; j++) {
				reflection.set(i, reflection.get(i).floatValue() 
						- lpc.get(j).floatValue() * autocorr.get(i - j).floatValue());
			}
			reflection.set(i, reflection.get(i).floatValue()/error);
			for (int j = 1; j < i; j++) {
                lpc.set(j, lpc.get(j).floatValue() 
                		+ reflection.get(i).floatValue()  * backwardPredictor.get(j).floatValue());
            }
			lpc.set(i, reflection.get(i).floatValue());
			error *= (1 - reflection.get(i).floatValue() * reflection.get(i).floatValue());
			if(error<=0.0){
				throw new ArithmeticException("no power left in signal! Error is less than 0: " + error);
			}
		}
//		List<Float> trimmed = lpc;//.subList(1, lpc.size()-1);
//		List<Float> reversed = MatrixUtils.reverseVector(trimmed);
		return lpc;
		
	}
	
}
