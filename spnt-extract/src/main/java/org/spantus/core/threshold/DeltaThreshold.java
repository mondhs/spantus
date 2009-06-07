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
	private Float maxThresholdPrevious;
	private MeanExtractor deltaMeanExtractor;
	private MeanExtractor valMeanExtractor;

	int increased = 0;
	int stableCount = 0;

	enum ChangeStatus {
		decreased, increased, stable
	}

	ChangeStatus changeStatus = ChangeStatus.stable;

	public DeltaThreshold() {
		deltaMeanExtractor = new MeanExtractor();
		deltaMeanExtractor.setOrder(20);
		valMeanExtractor = new MeanExtractor();
		valMeanExtractor.setOrder(20);
		valMeanExtractor.calculateMean(0F);
	}

	protected void processDiscriminator(Long sample, Float value) {
		Float prevState = 0F;
		Integer countChanges = 0;
		Integer countHits = 0;
		int i = 0;
		int statesInCount = Math.min(4, getState()
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
		Float coef = .4F;
		// if(changeRate>.3){
		// coef = .8F;
		// }else{
		// coef =.1F;
		// }

		maxThresholdPrevious = maxThresholdPrevious == null? 0:maxThresholdPrevious;
		previous = previous == null ? Float.MAX_VALUE : previous;
		Float delta = (value - previous);
		deltaMeanExtractor.calculateMean(Math.abs(delta));
		Float maxThreshold = deltaMeanExtractor.getMean() - coef
				* deltaMeanExtractor.getStdev();
		
//		Float minThreshold = deltaMeanExtractor.getMean() - (coef)
//				* deltaMeanExtractor.getStdev();
		Float noiseEstimation = valMeanExtractor.getMean() + .35F
		* valMeanExtractor.getStdev();
		noiseEstimation = Float.isInfinite(noiseEstimation)?Float.MAX_VALUE:noiseEstimation;

		float changeVal = (maxThreshold-maxThresholdPrevious)/maxThreshold;
		changeVal = Float.isNaN(changeVal)?0F:changeVal;
		changeVal = Float.isInfinite(changeVal)?0F:changeVal;
		
		boolean currentIncreasing = changeVal > .4;
//		boolean currentStabe = Math.abs(delta) > minThreshold
//				&& Math.abs(delta) < maxThreshold;
		boolean currentDecreasing =changeVal < - .4;
//		boolean currentStabe = !currentIncreasing && !currentDecreasing;
		Float state = 0F;
		int prevIncThr = 30;
		int prevStableThr = 10;
		if(false){
			
		}else if (currentIncreasing) {
			changeStatus = ChangeStatus.increased;
//			stableCount++;
//			increased = 1;
			stableCount = 1;
				
		} else if (currentDecreasing) {
//			increased = -1;
			changeStatus = ChangeStatus.decreased;
			stableCount = 0;

		}else{
			changeStatus = ChangeStatus.stable;
		}
		if(stableCount>0){
			stableCount+=statesInCount>2?1:0;
			if(value>valMeanExtractor.getStdev())
//			if (value > noiseEstimation*.1) {
				state=1F;
//			}else{
////				stableCount=0;
//			}
		}else{
			maxThresholdPrevious = 0F;
			valMeanExtractor.calculateMean(value);
		}
		

//		log.debug(
//				// "i:{0, number,###}; value:{1,number,#.000}; " +
//				"increasing:{3}; \t decreasing:{4}"
//						+ "; delta {2,number,#.000}; ", sample, value, delta,
//				currentIncreasing, currentDecreasing);

		getState().add(state);
		getThresholdValues().add(
		// 0F
//				 delta
//				 changeRate
//				changeRate
//				(float) changeStatus.ordinal()
//				((float) changeStatus.ordinal()*(float)deltaMeanExtractor.getStdev())
//				(changeVal)
//				(float)stableCount
				valMeanExtractor.getStdev()
//				noiseEstimation*.1F
//				(float)(deltaMeanExtractor.getStdev())
//				(100000F +(currentIncreasing?3000000F:0F)+(currentDecreasing?2000000F:0F))
//				(float)increased
//				(currentStabe?1000F:0F)
//				maxThreshold
//				Math.abs(delta)
//				delta
				 );
		previous = value;
		maxThresholdPrevious = maxThreshold;

	}

	@Override
	protected boolean isTrained() {
		return true;
	}

}
