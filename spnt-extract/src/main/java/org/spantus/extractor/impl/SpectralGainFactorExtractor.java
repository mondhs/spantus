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
import org.spantus.extractor.AbstractExtractor3D;
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
public class SpectralGainFactorExtractor extends AbstractExtractor3D {
//	List<Float> prior = null;
	List<Float> pCoefs = null;
	List<Float> aNoiseCoefs = null;
	
	
	FrameVectorValues bufferVals = new FrameVectorValues();
	FrameVectorValues smoothedSectrumVals = null;
	
	Float aACoef = 0.6F;
	Float aPCoef = 0.1F;
	Float betaCoef = .6F;
	Float Kcoef = 4F;
	int bufferSmmothingDepth = 3;
	
	AbstractExtractor3D abstractExtractor3D;
	float signalSampleRate;
	
	protected FrameVectorValues calculateFFT(FrameValues window){
		syncFFTParams();
		
		return getAbstractExtractor3D().calculateWindow(window);
	}
	
	private void syncFFTParams(){
		getAbstractExtractor3D().setConfig(getConfig());
	}
	
	/**
	 * 
	 */
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameVectorValues rtnValues = new FrameVectorValues();
		for (List<Float> currentSpectrum : val3d) {
			//calculate power spectrum averaged A(k,l)
			List<Float> avgSpectrum = calculateAvgSpectrum(currentSpectrum);
			if(smoothedSectrumVals == null){
				smoothedSectrumVals = new FrameVectorValues();
				smoothedSectrumVals.add(avgSpectrum);
			}
			//E(k,l) - recursive equation of first order
			List<Float> smoothedSpectrum = calculateSmoothedAvgSpectrum(smoothedSectrumVals, avgSpectrum);
			smoothedSectrumVals.add(smoothedSpectrum);
			if(smoothedSectrumVals.size()>bufferSmmothingDepth){
				smoothedSectrumVals.poll();	
			}
			//M(k,l)=min(E(k,l−i)); i=0..bufferSmmothingDepth
			List<Float> minNoise = calculateMinimumNoiseTracker(smoothedSectrumVals);
			
			//noise floor I(k,l)
			List<Boolean> noiseFloor = calculateNoiseFloor(smoothedSpectrum, minNoise, currentSpectrum);
			
			// N(k,l) = aNoise*N(k,l−1)+(1−aNoise)*|X(k,l)|
			List<Float> noiseEstimations = calculateNoiseEstimation(noiseFloor, currentSpectrum);
			rtnValues.add(noiseEstimations);
			
		}
		return rtnValues;
	}
	
	/**
	 * 
	 * @param current
	 * @return
	 */
	protected List<Float> calculateAvgSpectrum(List<Float> current){
		bufferVals.add(current);
		if(bufferVals.size()>3){
			bufferVals.poll();
		}	
		List<Float> avgs = MatrixUtils.zeros(current.size());
		for (List<Float> bfVal : bufferVals) {
			int i = 0;
			for (Float float1 : bfVal) {
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
	protected List<Float> calculateSmoothedAvgSpectrum(FrameVectorValues spectrums, List<Float> avgSpectrum){
		// prior(k,l)=b*prior(k,l-1)+(1-b)*current(k,l)
		// copy prior data
		Iterator<Float> priorAvgSpectrumIterator = new LinkedList<Float>(spectrums.getLast()).iterator();
		List<Float> rtnAvgSpectrum = new LinkedList<Float>();
		
		//calculate recursive equation of first order
		for (Float avgVal : avgSpectrum) {
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
	protected List<Float> calculateMinimumNoiseTracker(FrameVectorValues smoothedSectrums){
		List<Float> rtnMinNoiseTrack = null;
		//init min list
		if(rtnMinNoiseTrack == null){
			rtnMinNoiseTrack = new ArrayList<Float>();
			for (int i = 0; i < smoothedSectrums.getFirst().size(); i++) {
				rtnMinNoiseTrack.add(Float.MAX_VALUE);
			}
		}
		for (List<Float> list : smoothedSectrums) {
			int i = 0;
			for (Float float1 : list) {
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
	protected List<Float> getGainValues(List<Float> currentSpectrum){
		aNoiseCoefs = aNoiseCoefs == null?MatrixUtils.zeros(currentSpectrum.size()):aNoiseCoefs;
		List<Float> gains = new LinkedList<Float>();
		Iterator<Float> aNoiseCoefs = new LinkedList<Float>(currentSpectrum).iterator();
		for (Float power : currentSpectrum) {
			Float noiseCoef = aNoiseCoefs.next();
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
	protected List<Boolean> calculateNoiseFloor(List<Float> smoothedSpectrum, List<Float> minNoise,
			List<Float> currentSpectrum){
		List<Boolean> rtnNoiseFloor = new LinkedList<Boolean>();
		int binNum = 0;
		Iterator<Float> smoothedIter = smoothedSpectrum.iterator();
		Iterator<Float> gainIter = getGainValues(currentSpectrum).iterator();
		for (Float noise1 : minNoise) {
			Float gain = gainIter.next();
			//[1+K*e^(-G(k,l-1) )]*M(k,l)
			Double threshold = 1 + Kcoef * Math.exp(-gain);
			threshold *= noise1; 
			Float smoothedVal = smoothedIter.next();
			rtnNoiseFloor.add(smoothedVal>threshold);
			binNum++;
		}
		return rtnNoiseFloor;
	}
	/**
	 * 
	 * @param smoothedSpectrum
	 * @param minNoise
	 * @return
	 */
	protected List<Float> calculateNoiseEstimation(List<Boolean> noiseFloor, List<Float>currentSpectrum){
		
//		List<Float> rtnNoiseEstimation = new LinkedList<Float>();
		pCoefs = pCoefs == null?MatrixUtils.zeros(noiseFloor.size()):pCoefs;
		aNoiseCoefs = aNoiseCoefs == null?MatrixUtils.zeros(noiseFloor.size()):aNoiseCoefs;
		
		Iterator<Float> pCoefIter = new LinkedList<Float>(pCoefs).iterator();
		Iterator<Float> aNoiseCoefsIter = new LinkedList<Float>(aNoiseCoefs).iterator();
		Iterator<Float> currentSpectrumIter = new LinkedList<Float>(currentSpectrum).iterator();
		
		pCoefs.clear();
		aNoiseCoefs.clear();
		for (Boolean isSpeech : noiseFloor) {
			Float pParam = pCoefIter.next();
			Float aNoiseParam = 1F;
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
			Float noiseEstimation = aNoiseParam * aNoiseCoefsIter.next() 
				+ (1-aNoiseParam)*currentSpectrumIter.next();
			aNoiseCoefs.add(noiseEstimation);
		}
		return aNoiseCoefs;
	}


	public String getName() {
		return ExtractorEnum.SPECTRAL_GAIN_FACTOR.toString();
	}
	
	public AbstractExtractor3D getAbstractExtractor3D() {
		if(abstractExtractor3D == null){
			abstractExtractor3D = new FFTExtractor();
		}
		return abstractExtractor3D;
	}
}
