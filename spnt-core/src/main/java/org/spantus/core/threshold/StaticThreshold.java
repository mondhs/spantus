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

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
import org.spantus.logger.Logger;

public class StaticThreshold extends AbstractThreshold {

	private Logger log = Logger.getLogger(StaticThreshold.class);
	private int windowsLearned;
	private Long learningPeriod;
	private Double currentThresholdValue = Double.MIN_VALUE;
	private Double baseThresholdValue = null;

	public Double getCurrentThresholdValue() {
		return currentThresholdValue;
	}

	public void setCurrentThresholdValue(Double frameThreshold) {
		this.currentThresholdValue = frameThreshold;
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
	public Double calculateThreshold(Double value){
		if (!isTrained()) {
			baseThresholdValue = train(value, baseThresholdValue);
		}
		if(baseThresholdValue == null){
			baseThresholdValue = value;
		}
		Double rtnThreshold = applyCoef(baseThresholdValue);
		if(!isTrained() && rtnThreshold<value){
			currentThresholdValue = value;
			rtnThreshold = value;
		}
		currentThresholdValue = rtnThreshold;
		return rtnThreshold;
	}
	
	
	
	@Override
	public boolean isSignalState(Double value) {
		boolean isSignal = isTrained() && (currentThresholdValue<value);
		return isSignal;
	}
	
	protected boolean isTrained(){
		return windowsLearned > ( getExtractor().getExtractorSampleRate()*getLearningPeriod()/1000);
	}
	
	protected Double train(Double windowValue, Double thresholdValue){
		windowsLearned++;
		if(thresholdValue == null){
			log.debug("[train] {1} thresholdValue is null for {0}", windowValue, getName());
			thresholdValue = windowValue;
			return thresholdValue;
		}
		thresholdValue += windowValue;
		thresholdValue /=2;
		return thresholdValue;
	}

    @Override
    public String getRegistryName() {
        return getExtractor().getRegistryName();
    }

    @Override
    public ExtractorParam getParam() {
        return getExtractor().getParam();
    }

    @Override
    public void setParam(ExtractorParam param) {
        getExtractor().setParam(param);
    }

}
