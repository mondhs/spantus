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

package org.spantus.math.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.math.MatrixUtils;
import org.spantus.math.VectorUtils;
import org.spantus.math.services.PLPServiceSphinxImpl;

import edu.cmu.sphinx.spantus.frontend.transform.DiscreteFourierTransform;

public class PlpTest {
	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	List<Double> x;
	double[] mfccData =  new double[]{
			-2.7508381928662002, 3.5377788981783587, -9.698668856772205, 6.630642620376074, -2.7145711732056372, -2.3413094866154793, 2.9234692527998845, -1.982370166347817, -0.6955000406536725, 1.6206185119511574, -1.6933507398581757, 0.478056346492891
	};
	@Before
	public void setUp() throws Exception {
		x = MatrixUtils.zeros(256);
        x.set(2, 10D); x.set(4, 14D);

	}


    @Test
    public void testSphinxPLP(){
        x = new ArrayList<Double>();
		for (Double i = 1D; i < 64 * Math.PI; i+=.3) {
			x.add(new Double(Math.sin(i)));
		}

         int logm = (int) (Math.log(x.size()) / Math.log(2));
        int n = 1 << logm;

        DiscreteFourierTransform sFft = new DiscreteFourierTransform(n, false);
        sFft.initialize();
        Double[] fft = sFft.process(VectorUtils.toArray(new ArrayList<Double>(x)),16000);

        int bandSize = 13;
        PLPServiceSphinxImpl plpService = new PLPServiceSphinxImpl();
        
//        PLPFrequencyFilterBank lfccBanck = new PLPFrequencyFilterBank(130,6800, bandSize);
//		double[] sResult = lfccBanck.process(fft, 16000);
        List<Double> result = plpService.calculateFromSpectrum(
        		VectorUtils.toList(fft), 16000D);
		Assert.assertEquals(bandSize, result.size());
//		assertCollectionEqual(mfcc, mfccData);
		log.severe("testSphinxLPCC" + result);
	}

	
	public void assertCollectionEqual(List<Double> coll, double[] arr){
		if(coll.size() !=  arr.length){
			Assert.assertTrue(false);
		}
		for (int i = 0; i < arr.length; i++) {
			double double1 = arr[i];
			Assert.assertTrue("element not match" + i, equals(double1, coll.get(i)));
		}
	}
	public boolean equals( double d1, Double d2 ){
//		log.log(Level.SEVERE, " |" + d1 + "-" + d2 + "|=" + Math.abs(d1 - d2) );
		return Math.abs(d1 - d2) < 0.000001;
	}

}
