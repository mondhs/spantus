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

import java.util.HashSet;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorListener;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;

public abstract class AbstractClassifier implements IClassifier, IExtractorListener {
	
	private Logger log = Logger.getLogger(AbstractClassifier.class); 
	private IExtractor extractor;
	private FrameValues thereshold;
	private Float coef =null;
	private MarkerSet markSet;
	private Marker marker;
	private Set<IClassificationListener> classificationListeners;
	private long classifierSampleNum =0l;
	/**
	 * @param classifierSampleNum the classifierSampleNum to set
	 */
	public void setClassifierSampleNum(long classifierSampleNum) {
		this.classifierSampleNum = classifierSampleNum;
	}

	/**
	 * @return the classifierSampleNum
	 */
	public long getClassifierSampleNum() {
		return classifierSampleNum;
	}

	/**
	 * 
	 * @param windowValue
	 * @return
	 */
	public abstract Float calculateThreshold(Float windowValue);
	
	public abstract boolean isSignalState(Float windowValue);
	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.threshold.IClassifier#addClassificationListener(org.spantus.core.threshold.IClassificationListener)
	 */
	public boolean addClassificationListener(
			IClassificationListener classificationListener) {
		log.debug("[addClassificationListener]registering {0}",classificationListener);
		boolean result = getClassificationListeners().add(classificationListener);
		if(result){
			classificationListener.registered(getName());
		}
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.threshold.IClassifier#removeClassificationListener(org.spantus.core.threshold.IClassificationListener)
	 */
	public boolean removeClassificationListener(
			IClassificationListener classificationListener) {
		return getClassificationListeners().remove(classificationListener);
	}
	
	public IExtractor getExtractor() {
		return extractor;
	}
	
	public void setExtractor(IExtractor extractor) {
		this.extractor = extractor;
	}


	public FrameValues calculate(Long sample, FrameValues values) {
		return getExtractor().calculate(sample, values);
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues fv = getExtractor().calculateWindow(window);
		return fv;
	}
	/**
	 * Apply coef for given value
	 * @param value
	 * @return
	 */
	public Float applyCoef(Float value){
		return 	value + Math.abs(value* getCoef());

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
	
//	private Long prevSample;
	
	/**
	 * calculate State at the sample moment with given value
	 * 
	 * @param windowValue
	 * @param threshold
	 * @return
	 */
	protected void calculateState(Long sample, Float windowValue){
		Float timeFloat = getThresholdValues().toTime(sample.intValue())*1000;
		Long time = timeFloat.longValue();
		if(isSignalState(windowValue)){
			//segment
			if(getMarker()==null){
				setMarker(new Marker());
				getMarker().setStart(time);
				getMarker().setLabel(sample.toString());
//				getMarker().getExtractionData().setStartSampleNum(sample);
				for (IClassificationListener listener : getClassificationListeners()) {
					listener.onSegmentedStarted(
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
					listener.onSegmentedEnded(
							new SegmentEvent(getName(),time,getMarker(),sample));
				}
				setMarker(null);
			}
		}
		//notify that segment processed
		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentedProcessed(
					new SegmentEvent(getName(),time,getMarker(),sample));
		}
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
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getName();
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	public MarkerSet getMarkSet() {
		if(markSet == null){
			markSet = new MarkerSet();
			markSet.setMarkerSetType(MarkerSetHolderEnum.phone.name());
		}
		return markSet;
	}

	public void setMarkSet(MarkerSet markerSet) {
		this.markSet = markerSet;
	}
	public FrameValues getOutputValues() {
		return getExtractor().getOutputValues();
	}
	
	public FrameValues getThresholdValues() {
		if(thereshold == null){
			thereshold = new FrameValues();
		}
		return thereshold;
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

	public Set<IClassificationListener> getClassificationListeners() {
		if (classificationListeners == null) {
			classificationListeners =new HashSet<IClassificationListener>();
		}
		return classificationListeners;
	}

	public void setClassificationListeners(
			Set<IClassificationListener> classificationListeners) {
		this.classificationListeners = classificationListeners;
	}
	
}
