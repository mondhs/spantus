/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
package org.spantus.extractor.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.math.MatrixUtils;

/**
 * Based on article:
 * <i>Robust Noise Estimation Applied to Different Speech Estimators</i>
 * <b>Markus Schwab, Hyoung-Gook Kim, Wiryadi and Peter Noll</b>
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2009.06.02
 * 
 */
public class SpectralGainFactorExtractor extends AbstractExtractorVector {
//	List<Float> prior = null;
	List<Double> pCoefs = null;
	List<Double> aNoiseCoefs = null;
	
	
	FrameVectorValues bufferVals = new FrameVectorValues();
	FrameVectorValues smoothedSectrumVals = null;
	
	Double aACoef = 0.6D;
	Double aPCoef = 0.1D;
	Double betaCoef = .6D;
	Double Kcoef = 4D;
	int bufferSmmothingDepth = 3;
	
	AbstractExtractorVector abstractExtractorVector;
	float signalSampleRate;
	
	protected FrameVectorValues calculateFFT(FrameValues window){
		syncFFTParams();
		
		return getAbstractExtractorVector().calculateWindow(window);
	}
	
	private void syncFFTParams(){
		getAbstractExtractorVector().setConfig(getConfig());
	}
	
	/**
	 * 
	 */
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameVectorValues rtnValues = new FrameVectorValues();
		for (List<Double> currentSpectrum : val3d) {
			//calculate power spectrum averaged A(k,l)
			List<Double> avgSpectrum = calculateAvgSpectrum(currentSpectrum);
			if(smoothedSectrumVals == null){
				smoothedSectrumVals = new FrameVectorValues();
				smoothedSectrumVals.add(avgSpectrum);
			}
			//E(k,l) - recursive equation of first order
			List<Double> smoothedSpectrum = calculateSmoothedAvgSpectrum(smoothedSectrumVals, avgSpectrum);
			smoothedSectrumVals.add(smoothedSpectrum);
			if(smoothedSectrumVals.size()>bufferSmmothingDepth){
				smoothedSectrumVals.poll();	
			}
			//M(k,l)=min(E(k,l−i)); i=0..bufferSmmothingDepth
			List<Double> minNoise = calculateMinimumNoiseTracker(smoothedSectrumVals);
			
			//noise floor I(k,l)
			List<Boolean> noiseFloor = calculateNoiseFloor(smoothedSpectrum, minNoise, currentSpectrum);
			
			// N(k,l) = aNoise*N(k,l−1)+(1−aNoise)*|X(k,l)|
			List<Double> noiseEstimations = calculateNoiseEstimation(noiseFloor, currentSpectrum);
			rtnValues.add(noiseEstimations);
			
		}
		return rtnValues;
	}
	
	/**
	 * 
	 * @param currentSpectrum
	 * @return
	 */
	protected List<Double> calculateAvgSpectrum(List<Double> currentSpectrum){
		bufferVals.add(currentSpectrum);
		if(bufferVals.size()>3){
			bufferVals.poll();
		}	
		List<Double> avgs = MatrixUtils.zeros(currentSpectrum.size());
		for (List<Double> bfVal : bufferVals) {
			int i = 0;
			for (Double float1 : bfVal) {
				avgs.set(i, avgs.get(i)+float1);
				i++;
			}
		}
		return avgs;
	}
	/**
	 * 
	 * @param spectrums
	 * @param avgSpectrum
	 * @return
	 */
	protected List<Double> calculateSmoothedAvgSpectrum(FrameVectorValues spectrums, List<Double> avgSpectrum){
		// prior(k,l)=b*prior(k,l-1)+(1-b)*current(k,l)
		// copy prior data
		Iterator<Double> priorAvgSpectrumIterator = new LinkedList<Double>(spectrums.getLast()).iterator();
		List<Double> rtnAvgSpectrum = new LinkedList<Double>();
		
		//calculate recursive equation of first order
		for (Double avgVal : avgSpectrum) {
			//E(k,l) = betaCoef*E(k,l−1) + (1 − betaCoef)*A(k,l)
			rtnAvgSpectrum.add(betaCoef * priorAvgSpectrumIterator.next() + (1 - betaCoef)
					* avgVal);
		}
		return rtnAvgSpectrum;
	}
	/**
	 * 
	 * @param smoothedSectrums
	 * @return
	 */
	protected List<Double> calculateMinimumNoiseTracker(FrameVectorValues smoothedSectrums){
		List<Double> rtnMinNoiseTrack = null;
		//init min list
		if(rtnMinNoiseTrack == null){
			rtnMinNoiseTrack = new ArrayList<Double>();
			for (int i = 0; i < smoothedSectrums.getFirst().size(); i++) {
				rtnMinNoiseTrack.add(Double.MAX_VALUE);
			}
		}
		for (List<Double> list : smoothedSectrums) {
			int i = 0;
			for (Double float1 : list) {
				rtnMinNoiseTrack.set(i, 
						Math.min(rtnMinNoiseTrack.get(i), float1));
				i++;
			}
		}
		return rtnMinNoiseTrack;
	}
	/**
	 * Spectral Substracion Gain estimation
	 * @param binNum
	 * @return
	 */
	protected List<Float> getGainValues(List<Double> currentSpectrum){
		aNoiseCoefs = aNoiseCoefs == null?MatrixUtils.zeros(currentSpectrum.size()):aNoiseCoefs;
		List<Float> gains = new LinkedList<Float>();
		Iterator<Double> aNoiseCoefsIter = new LinkedList<Double>(currentSpectrum).iterator();
		for (Double power : currentSpectrum) {
			Double noiseCoef = aNoiseCoefsIter.next();
			Double gain = 1 - Math.sqrt(noiseCoef/power);
			gains.add(gain.floatValue());
		}
		return gains;
	}
	
	/**
	 * noise floor I(k,l)
	 * @param smoothedSpectrum
	 * @return
	 */
	protected List<Boolean> calculateNoiseFloor(List<Double> smoothedSpectrum, List<Double> minNoise,
			List<Double> currentSpectrum){
		List<Boolean> rtnNoiseFloor = new LinkedList<Boolean>();
		Iterator<Double> smoothedIter = smoothedSpectrum.iterator();
		Iterator<Float> gainIter = getGainValues(currentSpectrum).iterator();
		for (Double noise1 : minNoise) {
			Float gain = gainIter.next();
			//[1+K*e^(-G(k,l-1) )]*M(k,l)
			Double threshold = 1 + Kcoef * Math.exp(-gain);
			threshold *= noise1; 
			Double smoothedVal = smoothedIter.next();
			rtnNoiseFloor.add(smoothedVal>threshold);
		}
		return rtnNoiseFloor;
	}
	/**
	 * 
	 * @param smoothedSpectrum
	 * @param minNoise
	 * @return
	 */
	protected List<Double> calculateNoiseEstimation(List<Boolean> noiseFloor, List<Double> currentSpectrum){
		
//		List<Float> rtnNoiseEstimation = new LinkedList<Float>();
		pCoefs = pCoefs == null?MatrixUtils.zeros(noiseFloor.size()):pCoefs;
		aNoiseCoefs = aNoiseCoefs == null?MatrixUtils.zeros(noiseFloor.size()):aNoiseCoefs;
		
		Iterator<Double> pCoefIter = new LinkedList<Double>(pCoefs).iterator();
		Iterator<Double> aNoiseCoefsIter = new LinkedList<Double>(aNoiseCoefs).iterator();
		Iterator<Double> currentSpectrumIter = new LinkedList<Double>(currentSpectrum).iterator();
		
		pCoefs.clear();
		aNoiseCoefs.clear();
		for (Boolean isSpeech : noiseFloor) {
			Double pParam = pCoefIter.next();
			Double aNoiseParam = 1D;
			if(isSpeech){
//				p(k,l) = aPCoef + (1 − aPCoef ) * p(k,l−1)
//				aNoiseParam(k,l) = 1
				pParam = aPCoef + (1-aPCoef)*pParam;
			}else{
//				p(k, l) = (1 − aPCoef ) * p(k,l−1)
//				aNoiseParam(k, l) = aACoef + (1 − aACoef) * p(k, l)
				pParam = (1-aPCoef) + pParam;
				aNoiseParam = aACoef + (1-aNoiseParam)*pParam;
			}
			pCoefs.add(pParam);
			//N(k,l) = aNoiseParam * N(k,l−1) + (1−aNoiseParam)*|X(k,l)|
			Double noiseEstimation = aNoiseParam * aNoiseCoefsIter.next() 
				+ (1-aNoiseParam)*currentSpectrumIter.next();
			aNoiseCoefs.add(noiseEstimation);
		}
		return aNoiseCoefs;
	}


	public String getName() {
		return ExtractorEnum.SPECTRAL_GAIN_FACTOR.name();
	}
	
	public AbstractExtractorVector getAbstractExtractorVector() {
		if(abstractExtractorVector == null){
			abstractExtractorVector = ExtractorUtils.createFftExtractor();;
		}
		return abstractExtractorVector;
	}
}
