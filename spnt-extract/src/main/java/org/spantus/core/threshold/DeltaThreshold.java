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
 * @since 0.0.1
 * Created 2009.06.03
 *
 */
public class DeltaThreshold extends StaticThreshold {
	
//	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(DeltaThreshold.class);
	
	private Float previous;
	private MeanExtractor meanExtractor;
	
	int previousRising = 0;
	int previousDecreasing = 0;

	
	public DeltaThreshold() {
		meanExtractor = new MeanExtractor();
		meanExtractor.setOrder(50);
	}
	
	

	protected void processDiscriminator(Long sample, Float value){
		Float prevState = 0F;
		Integer countChanges = 0;
		int i = 0;
		int statesInCount = Math.min(meanExtractor.getOrder(),getState().size());
		for (ListIterator<Float> stateSubIter = getState().listIterator(getState().size()); stateSubIter.hasPrevious();) {
			Float iState = stateSubIter.previous(); 
			countChanges += !iState.equals(prevState)?1:0;
			if(i>statesInCount){ 
				break;
			}
			i++;
			prevState = iState;
		}
		Float changeRate = (countChanges/(float)statesInCount);
//		log.debug("changeRate:{0};", changeRate);
		Float coef = .75F;
//		if(changeRate>.3){
//			coef = .8F;
//		}else{
//			coef =.1F; 	
//		}
		
		
		previous = previous==null?value+value:previous;
		Float delta = (value-previous);
		meanExtractor.calculateMean(delta);
		Float threshold = meanExtractor.getMean()+coef*meanExtractor.getStdev();
		

		boolean currentRising = Math.abs(delta)>threshold && delta > 0;
		boolean currentDecreasing = Math.abs(delta)>threshold && delta < 0;
		Float state = 0F;
		int prevIncThr = 3; 
		int prevDecThr = 3;
		if(currentRising && previousRising<=prevIncThr){
			state = 0F;
			previousRising++;
		}else if(currentRising && previousRising>prevIncThr){
			state = 1F;
			previousRising++;
			previousDecreasing = 0;
			
		}else if(currentDecreasing && previousDecreasing<=prevDecThr){
			state = 1F;
			previousRising++;
			previousDecreasing++;
		}else if(currentDecreasing && previousDecreasing>prevDecThr){
			state = 0F;
			previousRising=0;
			previousDecreasing++;
		}else if(!currentRising && !currentDecreasing &&
				previousRising > 0 &&
				previousRising>previousDecreasing){
			state = 1F;
			previousRising++;
			previousDecreasing++;
		}else{
			state = 0F;
			previousDecreasing--;
		}
//			else{
//			//stable
//			if(previousRising==0){
////				state = 1F;
//				previousRising++;
//				previousDecreasing=0;
//			}else{
//				//was decreased
////				state = 0F;
//			}
//		}
		
		log.debug(
//				"i:{0, number,###}; value:{1,number,#.000}; " +
				"rising:{3}; \t decreasing:{4}"+
				"; delta {2,number,#.000}; "
				,sample
				,value
				,delta
				,currentRising
				,currentDecreasing
				);
		
		getState().add(state);
		getThresholdValues().add(0F);
		previous = value;
		
		
	}
	
	
		
	
	@Override
	protected boolean isTrained(){
		return true;
	}
	

	
}
