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
	
	private Logger log = Logger.getLogger(DeltaThreshold.class);
	
	private Float previous;
	private MeanExtractor meanExtractor;
	
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
		Float coef = 0F;
		if(changeRate>.3){
			coef = .8F;
		}else{
			coef =.1F; 	
		}
		
		
		previous = previous==null?value+value:previous;
		Float delta = Math.abs(value-previous);
		meanExtractor.calculateMean(delta);
		Float threshold = meanExtractor.getMean()+coef*meanExtractor.getStdev();
//		log.debug("i:{4, number,####}; value:{3,number,#.####}; " +
//				"delta {0,number,#.####}; threshold:{5}"//mean:{1,number,#.####}; stdev:{2,number,#.####}"
//				,delta, meanExtractor.getMean(), meanExtractor.getStdev()
//				,value
//				,sample
//				,threshold);

		previous = value;
		
		
		Float state = (value>threshold)?Float.valueOf(1f):Float.valueOf(0f);
		getState().add(state);
		
		getThresholdValues().add(changeRate);
	}
	
	
		
	
	@Override
	protected boolean isTrained(){
		return true;
	}
	

	
}
