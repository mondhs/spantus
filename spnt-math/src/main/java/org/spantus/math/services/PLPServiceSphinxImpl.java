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

import edu.cmu.sphinx.frontend.feature.BatchCMN;
import edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank;
import edu.cmu.sphinx.frontend.frequencywarp.PLPCepstrumProducer;
import edu.cmu.sphinx.frontend.frequencywarp.PLPFrequencyFilterBank;
import edu.cmu.sphinx.frontend.transform.DiscreteFourierTransform;

/**
 * based on
 * https://www.assembla.com/code/sonido/subversion/nodes/sphinx4/src/sphinx4
 * /edu/cmu/sphinx/frontend/frequencywarp/PLPCepstrumProducer.java?rev=11
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.3
 * 
 * 
 */
public class PLPServiceSphinxImpl implements PLPService {
	private int numberPLPFilters = 16;
	private int cepstrumSize = 13;
	private int LPCOrder = 14;
	private double[][] cosine;
	private PLPFrequencyFilterBank plpFrequencyFilterBank;
	private PLPCepstrumProducer plpCepstrumProducer;
	private BatchCMN batchCMN;

	public List<Float> calculate(List<Float> x, float sampleRate) {
		int logm = (int) (Math.log(x.size()) / Math.log(2));
		int n = 1 << logm;
		if (x.size() > n) {
			n = 1 << (logm + 1);
		}
		int missingSamples = n - x.size();
		x.addAll(Collections.nCopies(missingSamples, Float.valueOf(0f)));

		DiscreteFourierTransform sFft = new DiscreteFourierTransform(n, false);
		sFft.initialize();
		double[] fftArr = sFft.process(VectorUtils
				.toDoubleArray(new ArrayList<Float>(x)), (int) sampleRate);

//		double[] sResult = getPlpFrequencyFilterBank().process(fft,
//				(int) sampleRate);
		List<Float> fft = VectorUtils.toFloatList(fftArr);

		return calculateFromSpectrum(fft, sampleRate);
	}

	/**
	 * calculate MFCC from spectrum
	 * 
	 * @param fft
	 * @param sampleRate
	 * @return
	 */
	public List<Float> calculateFromSpectrum(List<Float> fft, float sampleRate) {

		int intSampleRate = (int) sampleRate;
		double[] plps = getPlpFrequencyFilterBank().process(
				VectorUtils.toDoubleArray(fft), intSampleRate);
		double[] lfcc = getPlpCepstrumProducer().process(plps, intSampleRate);
		double[] normalized = getBatchCMN().process(lfcc);

		List<Float> result = VectorUtils.toFloatList(normalized);

		return result;
	}

//	/** Compute the Cosine values for IDCT. */
//	private void computeCosine() {
//		cosine = new double[LPCOrder + 1][numberPLPFilters];
//
//		double period = (double) 2 * numberPLPFilters;
//
//		for (int i = 0; i <= LPCOrder; i++) {
//			double frequency = 2 * Math.PI * (double) i / period;
//
//			for (int j = 0; j < numberPLPFilters; j++) {
//				cosine[i][j] = Math.cos(frequency * (j + 0.5));
//			}
//		}
//	}

	public BatchCMN getBatchCMN() {
		if(batchCMN == null){
			batchCMN = new BatchCMN();
			batchCMN.initialize();
		}
		return batchCMN;
	}

	public void setBatchCMN(BatchCMN batchCMN) {
		this.batchCMN = batchCMN;
	}

	public PLPFrequencyFilterBank getPlpFrequencyFilterBank() {
		if(plpFrequencyFilterBank == null){
			plpFrequencyFilterBank = new PLPFrequencyFilterBank(130,6800, numberPLPFilters);
	    }
		return plpFrequencyFilterBank;
	}

	public void setPlpFrequencyFilterBank(
			PLPFrequencyFilterBank plpFrequencyFilterBank) {
		this.plpFrequencyFilterBank = plpFrequencyFilterBank;
	}

	public PLPCepstrumProducer getPlpCepstrumProducer() {
		if(plpCepstrumProducer == null){
			plpCepstrumProducer = new PLPCepstrumProducer();
		}
		return plpCepstrumProducer;
	}

	public void setPlpCepstrumProducer(PLPCepstrumProducer plpCepstrumProducer) {
		this.plpCepstrumProducer = plpCepstrumProducer;
	}
}
