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

package org.spantus.math.services;


import org.spantus.math.MFCC;

import java.util.Collections;
import java.util.List;



/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.09.28
 *  
 */
public class MFCCServiceImpl implements MFCCService {

	
	public List<Double> calculateMFCC(List<Double> x, Double sampleRate) {
		int logm = (int) (Math.log(x.size()) / Math.log(2));
		int n = 1 << logm;
		if(x.size() > n){
			n = 1 << (logm+1);
		}
		int missingSamples = n - x.size();
		x.addAll(Collections.nCopies(missingSamples, 0.0));

		List<Double> floats = MFCC.calculateMFCC(x,
				sampleRate);

		return floats;
	}
    public List<Double> calculateMfccFromSpectrum(List<Double> fft, Double sampleRate){
        throw new IllegalAccessError("Not impl");
    }

}
