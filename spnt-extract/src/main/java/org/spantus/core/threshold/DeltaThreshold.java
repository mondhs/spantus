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
package org.spantus.core.threshold;

import java.util.ListIterator;

import org.spantus.extractor.impl.MeanExtractor;
import org.spantus.logger.Logger;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created 2009.06.03
 * 
 */
public class DeltaThreshold extends StaticThreshold {

	 @SuppressWarnings("unused")
	private Logger log = Logger.getLogger(DeltaThreshold.class);

	private Float previous;
	private Float estimate = 0F;
	private Float noiseEstimate = 0F;
	private Float noiseThreshold = 0F;
	private Float previousDelta = 0F;
	private MeanExtractor meanExtractor;

	int increased = 0;
	int stableCount = 0;

	enum ChangeStatus {
		increased, decreased, stable
	}

	ChangeStatus changeStatus = ChangeStatus.stable;

	public DeltaThreshold() {
		meanExtractor = new MeanExtractor();
		meanExtractor.setOrder(50);
	}

	protected void processDiscriminator(Long sample, Float value) {
		Float prevState = 0F;
		Integer countChanges = 0;
		Integer countHits = 0;
		int i = 0;
		int statesInCount = Math.min(meanExtractor.getOrder(), getState()
				.size());
		for (ListIterator<Float> stateSubIter = getState().listIterator(
				getState().size()); stateSubIter.hasPrevious();) {
			Float iState = stateSubIter.previous();
			countChanges += iState-prevState != 0 ? 1 : 0;
			countHits += iState;
			if (i > statesInCount) {
				break;
			}
			i++;
			prevState = iState;
		}
		Float changeRate = (countChanges / (float) (statesInCount+2));
		Float hitRate = (countHits / (float) (statesInCount+2));
		// log.debug("changeRate:{0};", changeRate);
		Float coef = .75F;
		// if(changeRate>.3){
		// coef = .8F;
		// }else{
		// coef =.1F;
		// }

		previous = previous == null ? 2*value : previous;
		Float delta = (value - previous);
		meanExtractor.calculateMean(delta);
		Float maxThreshold = meanExtractor.getMean() + coef
				* meanExtractor.getStdev();
		Float minThreshold = meanExtractor.getMean() - (1.4F - coef)
				* meanExtractor.getStdev();
		

		boolean currentIncreasing = Math.abs(delta) > maxThreshold && delta > 0;
		boolean currentStabe = Math.abs(delta) > minThreshold
				&& Math.abs(delta) < maxThreshold;
		boolean currentDecreasing = Math.abs(delta) > maxThreshold && delta < 0;
		Float state = 0F;
		int prevIncThr = 30;
		int prevStableThr = 10;
	

//		log.debug(
//				// "i:{0, number,###}; value:{1,number,#.000}; " +
//				"increasing:{3}; \t decreasing:{4}"
//						+ "; delta {2,number,#.000}; ", sample, value, delta,
//				currentIncreasing, currentDecreasing);

		Float BEstimateCoef = 0.9992F; 
		Float BNoiseCoef = 0.9922F;
		Float BThresholdCoef = 0.799975F;

		
		//emphaseValue  - u(k)
		Float emphaseValue = Math.abs(value);//Math.abs(value - .95F*previous);
		//estimate - s(k)
		if(estimate>emphaseValue){
			estimate = emphaseValue;			
		}else{
			estimate = (1-BEstimateCoef) * emphaseValue + BEstimateCoef * estimate;
		}
		
		
		//noiseEstimate - n(k)
		if(noiseEstimate>emphaseValue){
			noiseEstimate = emphaseValue;
		}else{
			noiseEstimate = (1-BNoiseCoef) * emphaseValue + BNoiseCoef * noiseEstimate;
		}
		
		
		//noiseThreshold - tn(k)
		if(noiseThreshold>noiseEstimate){
			noiseThreshold = (1-BThresholdCoef) * noiseEstimate + BThresholdCoef * noiseThreshold;
		}else{
			noiseThreshold = noiseEstimate;
		}
		//thresholdSpeech - T_s
		Float ThresholdSpeech = 2.0F;
		//thresholdSpeech - T_n
		Float ThresholdNose = 1.414F;
		Float ThresholdMin = 1F;
		//s(k)> T_s*tn(k)+T_min
//		if(estimate>ThresholdSpeech*noiseThreshold+ThresholdMin){
//			state = 1F;
//		}else if(estimate<ThresholdNose*noiseThreshold+ThresholdMin){
//			state = 0F;
//		}else{
//			//do nothing
//			getClass();
//		}
		if(estimate*2<value){
			state = 1F;	
		}
		
		getState().add(state);
		getThresholdValues().add(
		// 0F
//				 delta 
//				 changeRate
//				 hitRate
//				(float) changeStatus.ordinal()
//				 meanExtractor.getStdev()
				estimate*2
				 );
		
		previousDelta =delta;
		previous = value;
	}

	@Override
	protected boolean isTrained() {
		return true;
	}

}
