package org.spantus.core.threshold;

import org.spantus.core.extractor.IExtractor;

public class StaticThreshold extends AbstractClassifier {

	private int windowsLearned;
	private Long learningPeriod;
	private Float currentThresholdValue = Float.MIN_VALUE;

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
	 * 
	 * @param windowValue
	 * @return
	 */
	public Float calculateThreshold(Float windowValue){
		if (!isTrained()) {
			currentThresholdValue = train(windowValue, currentThresholdValue);
		}
		//with negative values this should work too
		return currentThresholdValue + (Math.abs(currentThresholdValue* getCoef()));
	}
	
	protected boolean isTrained(){
		return windowsLearned > ( getExtractor().getExtractorSampleRate()*getLearningPeriod()/1000);
	}
	
	protected Float train(Float windowValue, Float thresholdValue){
		windowsLearned++;
		if(Float.MIN_VALUE == thresholdValue){
			thresholdValue = windowValue;
			return thresholdValue;
		}
		thresholdValue += windowValue;
		thresholdValue /=2;
		return thresholdValue;
	}

}
