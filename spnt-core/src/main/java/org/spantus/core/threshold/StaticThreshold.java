/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.threshold;

import org.spantus.core.extractor.IExtractor;

public class StaticThreshold extends AbstractClassifier {

	private int windowsLearned;
	private Long learningPeriod;
	private Float currentThresholdValue = Float.MIN_VALUE;
	private Float baseThresholdValue = null;

	public Float getCurrentThresholdValue() {
		return currentThresholdValue;
	}

	public void setCurrentThresholdValue(Float currentThresholdValue) {
		this.currentThresholdValue = currentThresholdValue;
	}

	public Long getLearningPeriod() {
		if(learningPeriod == null){
			learningPeriod = 250L;//0.25s
		}
		return learningPeriod;
	}

	public void setLearningPeriod(Long learningPeriod) {
		this.learningPeriod = learningPeriod;
	}

	
	@Override
	public void setExtractor(IExtractor extractor) {
		super.setExtractor(extractor);
		windowsLearned = 0;
	}

	/**
	 * currentThresholdValue attribute should be set after calculation 
	 * 
	 * @param windowValue
	 * @return calculate threshold value
	 */
	public Float calculateThreshold(Float value){
		if (!isTrained()) {
			baseThresholdValue = train(value, baseThresholdValue);
		}
		if(baseThresholdValue == null){
			baseThresholdValue = value;
		}
		Float rtnThreshold = null;
//		if(getCoef()>1){
			rtnThreshold =baseThresholdValue + Math.abs(baseThresholdValue* getCoef());
//		}else{
//			rtnThreshold = baseThresholdValue* getCoef();
//		}
		//with negative values this should work too
		if(!isTrained() && rtnThreshold<value){
			currentThresholdValue = value;
			rtnThreshold = value;
		}
		currentThresholdValue = rtnThreshold;
		return rtnThreshold;
	}
	
	
	
	@Override
	public boolean isSignalState(Float value) {
		boolean isSignal = isTrained() && (currentThresholdValue<value);
		return isSignal;
	}
	
	protected boolean isTrained(){
		return windowsLearned > ( getExtractor().getExtractorSampleRate()*getLearningPeriod()/1000);
	}
	
	protected Float train(Float windowValue, Float thresholdValue){
		windowsLearned++;
		if(thresholdValue == null){
			thresholdValue = windowValue;
			return thresholdValue;
		}
		thresholdValue += windowValue;
		thresholdValue /=2;
		return thresholdValue;
	}

}
