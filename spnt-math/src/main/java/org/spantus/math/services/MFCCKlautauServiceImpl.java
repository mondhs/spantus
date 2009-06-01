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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import klautau.MFCC;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.05.24
 *  
 */
public class MFCCKlautauServiceImpl implements MFCCService {

	
	public List<Float> calculateMFCC(List<Float> x, double sampleRate) {
		int logm = (int) (Math.log(x.size()) / Math.log(2));
		int n = 1 << logm;
		if(x.size() > n){
			n = 1 << (logm+1);
		}
		int missingSamples = n - x.size();
		x.addAll(Collections.nCopies(missingSamples, Float.valueOf(0f)));
//
//		List<Float> floats = MFCC.calculateMFCC(x,
//				sampleRate);
		 int nnumberofFilters = 24; 
         int nlifteringCoefficient = 22; 
         boolean oisLifteringEnabled = true; 
         boolean oisZeroThCepstralCoefficientCalculated = false; 
         int nnumberOfMFCCParameters = 12; //without considering 0-th 
         double dsamplingFrequency = sampleRate; 
         int nFFTLength = x.size() ; 
         if (oisZeroThCepstralCoefficientCalculated) { 
           //take in account the zero-th MFCC 
           nnumberOfMFCCParameters = nnumberOfMFCCParameters + 1; 
         } 

         MFCC mfcc = new MFCC(nnumberOfMFCCParameters, 
                              dsamplingFrequency, 
                              nnumberofFilters, 
                              nFFTLength, 
                              oisLifteringEnabled, 
                              nlifteringCoefficient, 
                              oisZeroThCepstralCoefficientCalculated); 
        double[] fspeechFrame = new double[x.size()];
        int i = 0;
        for (Float float1 : x) {
			fspeechFrame[i++]=float1.doubleValue();
		}
       
        double[] mfccVal = mfcc.getParameters(fspeechFrame);
        
        List<Float> mfccList = new LinkedList<Float>();
        for (Double d : mfccVal) {
        	mfccList.add(d.floatValue());
		}
		return mfccList;
	}

}
