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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spantus.math.VectorUtils;

import edu.cmu.sphinx.frontend.transform.DiscreteFourierTransform;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.09.28
 *  
 */
public class FFTServiceSphinxImpl implements FFTService{
    private DiscreteFourierTransform sFft = null;
    private int numberFftPoints = 0;

	public List<Float> calculateFFTMagnitude(long index, List<Float> x, Float sampleRate) {
		List<Float> fftInput= new ArrayList<Float>(x);
		int logm = (int) (Math.log(fftInput.size()) / Math.log(2));
		int n = 1 << logm;
		if(fftInput.size() > n){
			n = 1 << (logm+1);
		}
		int missingSamples = n - x.size();
		fftInput.addAll(Collections.nCopies(missingSamples, Float.valueOf(0f)));
//		Float[] fftArr = fftInput.toArray(new Float[fftInput.size()]);
        if(numberFftPoints!=n){
            numberFftPoints = n;
            sFft = new DiscreteFourierTransform(n, false);
            sFft.initialize();
        }

        double[] sResult = sFft.process(VectorUtils.toDoubleArray(x),sampleRate.intValue());
        List<Float> fftOutput = VectorUtils.toFloatList(sResult);
		return fftOutput;
	}

}
