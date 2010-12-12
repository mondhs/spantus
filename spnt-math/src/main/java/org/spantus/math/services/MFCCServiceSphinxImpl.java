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


import edu.cmu.sphinx.frontend.feature.BatchCMN;
import edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank;
import edu.cmu.sphinx.frontend.transform.DiscreteCosineTransform;
import edu.cmu.sphinx.frontend.transform.DiscreteFourierTransform;
import org.spantus.math.VectorUtils;

import java.util.ArrayList;
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
public class MFCCServiceSphinxImpl implements MFCCService {
    private int numberMelFilters = 20;
     private int cepstrumSize = 13;
    private DiscreteCosineTransform dct;
    private FFTServiceSphinxImpl fftService;
    private MelFrequencyFilterBank melFrequencyFilterBank;
    private BatchCMN batchCMN;

	
	public List<Float> calculateMFCC(List<Float> x, float sampleRate) {
		int logm = (int) (Math.log(x.size()) / Math.log(2));
		int n = 1 << logm;
		if(x.size() > n){
			n = 1 << (logm+1);
		}
		int missingSamples = n - x.size();
		x.addAll(Collections.nCopies(missingSamples, Float.valueOf(0f)));

        DiscreteFourierTransform sFft = new DiscreteFourierTransform(n, false);
        sFft.initialize();
        double[] fft = sFft.process(VectorUtils.toDoubleArray(new ArrayList<Float>(x)),(int)sampleRate);

		double[] sResult = getMelFrequencyFilterBank().process(fft, (int)sampleRate);
        List<Float> result = VectorUtils.toFloatList(sResult);

		return result;
	}

    /**
     * calculate MFCC from spectrum
     * @param fft
     * @param sampleRate
     * @return
     */
    public List<Float> calculateMfccFromSpectrum
            (List<Float> fft, float sampleRate) {


		double[] mels = getMelFrequencyFilterBank().process(VectorUtils.toDoubleArray(fft), (int)sampleRate);
        double[] mfcc = getDct().process(mels);
        double[] normalized = getBatchCMN().process(mfcc);
        List<Float> result = VectorUtils.toFloatList(normalized);

		return result;
	}

    public DiscreteCosineTransform getDct() {
        if(dct == null){
          dct =new DiscreteCosineTransform(numberMelFilters, cepstrumSize);
           dct.initialize();
        }
        return dct;
    }


    public void setDct(DiscreteCosineTransform dct) {
        this.dct = dct;

    }
    public MelFrequencyFilterBank getMelFrequencyFilterBank() {
        if(melFrequencyFilterBank == null){
           melFrequencyFilterBank = new MelFrequencyFilterBank(130,6800, numberMelFilters);
        }
        return melFrequencyFilterBank;
    }

    public void setMelFrequencyFilterBank(MelFrequencyFilterBank melFrequencyFilterBank) {
        this.melFrequencyFilterBank = melFrequencyFilterBank;
    }

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
}
