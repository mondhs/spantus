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
package org.spantus.math.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import klautau.FFT;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.09.28
 *  
 */
public class FFTServiceImpl implements FFTService{


	public List<Double> calculateFFTMagnitude(long index, List<Double> x, Double sampleRate) {
		List<Double> fftInput= new LinkedList<Double>(x);
		int logm = (int) (Math.log(fftInput.size()) / Math.log(2));
		int n = 1 << logm;
		if(fftInput.size() > n){
			n = 1 << (logm+1);
		}
		int missingSamples = n - x.size();
		fftInput.addAll(Collections.nCopies(missingSamples, Double.valueOf(0)));
		Double[] fftArr = fftInput.toArray(new Double[fftInput.size()]);
		FFT m_fft = new FFT(n);
		
		Double[] fftOutput = m_fft.calculateFFTPower(fftArr);
		return Arrays.asList(fftOutput);
	}
}
