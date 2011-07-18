package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.utils.Assert;

public abstract class AbstractThreshold extends AbstractClassifier{
	private FrameValues thereshold;
	private Double coef =null;
	/**
	 * 
	 * @param double1
	 * @return
	 */
	public abstract Double calculateThreshold(Double double1);
	
	public abstract boolean isSignalState(Double double1);

	
	/**
	 * 
	 */
	protected void processDiscriminator(Long sample, Double double1){
		Double threshold = calculateThreshold(double1);
		if(threshold != null){
			getThresholdValues().add(threshold);
			calculateState(getClassifierSampleNum(), double1);
		}
		setClassifierSampleNum(getClassifierSampleNum() + 1);
	}

	public void afterCalculated(Long sample, FrameValues result) {
		getThresholdValues().setSampleRate(getExtractorSampleRate());
		for (Double float1 : result) {
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
	 * @param double1
	 * @param threshold
	 * @return
	 */
	protected void calculateState(Long sample, Double double1){
		Long time = getThresholdValues().indextoMils(sample.intValue());
		if(isSignalState(double1)){
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
	 * @param baseThresholdValue
	 * @return
	 */
	public Double applyCoef(Double baseThresholdValue){
		return 	baseThresholdValue + Math.abs(baseThresholdValue* getCoef());

	}

	
	
	public FrameValues getThresholdValues() {
		if(thereshold == null){
			thereshold = new FrameValues();
		}
		return thereshold;
	}

	public Double getCoef() {
		if(coef == null){
			coef = 0.1D;//*10%
		}
		return coef;
	}

	public void setCoef(Double coef) {
		this.coef = coef;
	}

}
