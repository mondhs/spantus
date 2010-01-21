package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorListener;
import org.spantus.core.marker.MarkerSet;
import org.spantus.utils.Assert;

public abstract class AbstractClassifier implements IClassifier, IExtractorListener {
	
	private IExtractor extractor;
	private FrameValues thereshold;
	private Float coef =null;
	private FrameValues state;
	
	/**
	 * 
	 * @param windowValue
	 * @return
	 */
	public abstract Float calculateThreshold(Float windowValue);
	
	public IExtractor getExtractor() {
		return extractor;
	}
	
	public MarkerSet getMarkerSet() {
		return null;
	}

	public void setExtractor(IExtractor extractor) {
		this.extractor = extractor;
	}

	public FrameValues getThresholdValues() {
		if(thereshold == null){
			thereshold = new FrameValues();
		}
		return thereshold;
	}

	public FrameValues calculate(Long sample, FrameValues values) {
		return getExtractor().calculate(sample, values);
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues fv = getExtractor().calculateWindow(window);
		return fv;
	}

	public FrameValues getOutputValues() {
		return getExtractor().getOutputValues();
	}

	public IExtractorConfig getConfig() {
		return getExtractor().getConfig();
	}

	public float getExtractorSampleRate() {
		return getExtractor().getExtractorSampleRate();
	}

	public String getName() {
		return getExtractor().getName();
	}

	public void putValues(Long sample, FrameValues values) {
		getExtractor().putValues(sample, values);
	}

	public void setConfig(IExtractorConfig config) {
		getExtractor().setConfig(config);		
	}

	public void afterCalculated(Long sample, FrameValues result) {
		getThresholdValues().setSampleRate(getExtractorSampleRate());
		for (Float float1 : result) {
			processDiscriminator(sample, float1);
		}
		cleanup();
	}
	protected void cleanup(){
		getState().setSampleRate(getExtractorSampleRate());
		Assert.isTrue(getConfig() != null, "cofiguration not set");
		int i = getThresholdValues().size() - getConfig().getBufferSize();
		while( i > 0 ){
			getThresholdValues().poll();
			getState().poll();
			i--;
		}
	}
	/**
	 * 
	 */
	protected void processDiscriminator(Long sample, Float float1){
		Float threshold = calculateThreshold(float1);
		if(threshold != null){
			getThresholdValues().add(threshold);
			getState().add(calculateState(sample, float1, threshold));
		}
	}
	
	/**
	 * 
	 */
	public void beforeCalculated(Long sample, FrameValues window) {
		//do nothing
	}
	/**
	 * 
	 */
	public void flush() {
		getExtractor().flush();
	}
	
	/**
	 * 
	 * @param windowValue
	 * @param threshold
	 * @return
	 */
	protected Float calculateState(Long sample, Float windowValue, Float threshold){
		if(threshold == null) return null;
		return (windowValue>threshold)?Float.valueOf(1f):Float.valueOf(0f); 
	}
	
	public FrameValues getState() {
		if(state == null){
			state = new FrameValues();
		}
		return state;
	}


	public Float getCoef() {
		if(coef == null){
			coef = 0.1F;//*10%
		}
		return coef;
	}

	public void setCoef(Float coef) {
		this.coef = coef;
		if(this.coef>=1){
			this.coef = this.coef -1F;
		}
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getName();
	}

	
}
