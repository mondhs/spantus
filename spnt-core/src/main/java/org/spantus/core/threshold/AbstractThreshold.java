package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.utils.Assert;

public abstract class AbstractThreshold extends AbstractClassifier{
	private FrameValues thereshold;
	private Float coef =null;
	/**
	 * 
	 * @param windowValue
	 * @return
	 */
	public abstract Float calculateThreshold(Float windowValue);
	
	public abstract boolean isSignalState(Float windowValue);

	
	/**
	 * 
	 */
	protected void processDiscriminator(Long sample, Float float1){
		Float threshold = calculateThreshold(float1);
		if(threshold != null){
			getThresholdValues().add(threshold);
			calculateState(getClassifierSampleNum(), float1);
		}
		setClassifierSampleNum(getClassifierSampleNum() + 1);
	}

	public void afterCalculated(Long sample, FrameValues result) {
		getThresholdValues().setSampleRate(getExtractorSampleRate());
		for (Float float1 : result) {
			processDiscriminator(sample, float1);
		}
		cleanup();
	}
	protected void cleanup(){
//		getState().setSampleRate(getExtractorSampleRate());
		Assert.isTrue(getConfig() != null, "cofiguration not set");
		int i = getThresholdValues().size() - getConfig().getBufferSize();
		while( i > 0 ){
			getThresholdValues().poll();
//			getState().poll();
			i--;
		}
	}

	
	/**
	 * calculate State at the sample moment with given value
	 * 
	 * @param windowValue
	 * @param threshold
	 * @return
	 */
	protected void calculateState(Long sample, Float windowValue){
		Long time = getThresholdValues().indextoMils(sample.intValue());
		if(isSignalState(windowValue)){
			//segment
			if(getMarker()==null){
				setMarker(new Marker());
				getMarker().setStart(time);
				getMarker().setLabel(sample.toString());
//				getMarker().getExtractionData().setStartSampleNum(sample);
				for (IClassificationListener listener : getClassificationListeners()) {
					listener.onSegmentStarted(
							new SegmentEvent(getName(),time,getMarker(),sample));
				}
			}
		}else {
			//silent
			if(getMarker()!=null){
				getMarker().setEnd(time);
				getMarkSet().getMarkers().add(getMarker());
//				getMarker().getExtractionData().setEndSampleNum(sample);
				for (IClassificationListener listener : getClassificationListeners()) {
					listener.onSegmentEnded(
							new SegmentEvent(getName(),time,getMarker(),sample));
				}
				setMarker(null);
			}
		}
		//notify that segment processed
		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentProcessed(
					new SegmentEvent(getName(),time,getMarker(),sample));
		}
	}

	
	/**
	 * Apply coef for given value
	 * @param value
	 * @return
	 */
	public Float applyCoef(Float value){
		return 	value + Math.abs(value* getCoef());

	}

	
	
	public FrameValues getThresholdValues() {
		if(thereshold == null){
			thereshold = new FrameValues();
		}
		return thereshold;
	}

	public Float getCoef() {
		if(coef == null){
			coef = 0.1F;//*10%
		}
		return coef;
	}

	public void setCoef(Float coef) {
		this.coef = coef;
	}

}
