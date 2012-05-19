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
package org.spantus.segment.online;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;

import scikit.util.Pair;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.11.27
 * 
 */

public class MultipleSegmentatorListenerOnline implements ISegmentatorListener {

	private static final Logger LOG = Logger
			.getLogger(MultipleSegmentatorListenerOnline.class);

	private LinkedHashMap<Long, Integer> sequenceSignal;  
	private LinkedHashMap<Long, Integer> sequenceNoise;
	private Integer classifiersCount = 0; 
	private Integer classifiersThreshold = 0;
	private Marker currentMarker;
	private MarkerSet markerSet;

	private IExtractorConfig config;
	
	public MarkerSet getMarkSet() {
		if(markerSet == null){
			markerSet = new MarkerSet(); 
			markerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());
		}
		return markerSet;
	}

	public void onSegmentStarted(SegmentEvent event) {
		//do nothing
	}

	public void onSegmentEnded(SegmentEvent event) {
		//do nothing
	}
	public void onNoiseProcessed(SegmentEvent event) {
		//do nothing
	}
	/**
	 * 
	 */
	public void onSegmentProcessed(final SegmentEvent event) {
		LOG.debug("[onSegmentedProcessed] +++ {0}", event);
		Pair<Integer, Integer> signalNoiseCount= updatesignalNoiseCount(event.getTime(), event.getSignalState());
		Integer singnalCount = signalNoiseCount.fst();
		Integer noiseCount = signalNoiseCount.snd();
		Integer totalCount =  singnalCount+noiseCount;
		if(totalCount == classifiersCount){
			LOG.debug("[onSegmentedProcessed] time: {0}; noiseCount={1}; singnalCount={2} ", event.getTime(), noiseCount, singnalCount);
			if(noiseCount>classifiersThreshold){
				noiseDetected(event);
			}else if(singnalCount>classifiersThreshold){
				segmentDetected(event);
			}else if (singnalCount == classifiersThreshold && singnalCount==noiseCount ) {
				noiseDetected(event);
			}else {
				throw new IllegalArgumentException("Not impl");
//				multievent.setMarker(getCurrentMarker());
//				segmentProcessedMulti(multievent);
				
			}
		}
		finazlizeMarker(getCurrentMarker(), event);
		LOG.debug("[onSegmentedProcessed] --- {0}", event);
	}
	/**
	 * 
	 * @param time 
	 * @return
	 */
	private Pair<Integer, Integer> updatesignalNoiseCount(Long time, boolean signal) {
		
		Integer singnalCount = getSequenceSignal().get(time);
		Integer noiseCount = getSequenceNoise().get(time);
		if(singnalCount==null){
			singnalCount = 0;
			noiseCount = 0;
			getSequenceSignal().put(time, singnalCount);
			getSequenceNoise().put(time, noiseCount);
		}
		if (signal) {
			getSequenceSignal().put(time,++singnalCount );
		} else {
			getSequenceNoise().put(time,++noiseCount );
		}
		Pair<Integer, Integer> signalNoiseCount = new Pair<Integer, Integer>(singnalCount, noiseCount);
		return signalNoiseCount;
	}

	protected void segmentDetected(SegmentEvent event){
		if(getCurrentMarker() == null){
			setCurrentMarker(createMarker(event));
		}
		LOG.debug("[segmentDetected] started {0} on {1}ms", currentMarker, event.getTime());
	}
	protected void noiseDetected(final SegmentEvent event){
		Marker aMarker = null;
		if(getCurrentMarker() != null){
			getCurrentMarker().setEnd(event.getTime());
			onSegmentEnded(getCurrentMarker());
		}
		LOG.debug("[noiseDetected] ended {0} on {1}ms", aMarker, event.getTime());
		
	}
//	protected void segmentProcessedMulti(SegmentEvent event){
//		log.debug("[segmentProcessedMulti] processed {0} on {1}ms", currentMarker, event.getTime());
//	}

	
	protected Marker createMarker(SegmentEvent event) {
		Marker marker = new Marker();
		marker.setStart(event.getTime());
		marker.setLabel(""+event.getTime());
//		log.debug("[createSegment] marker({0}ms): {1}", time, marker.toString());
		return marker;
	}
	protected Marker finazlizeMarker(Marker marker,final SegmentEvent event) {
		if (marker == null){
			return marker;
		}
		Long endTime = event.getTime();
		if(marker.getStart() == endTime){
			endTime++;
		}
		marker.setEnd(endTime);
//		log.debug("[finazlizeSegment] marker({0}ms): {1}", time, marker.toString());
		return marker;
	}
	
	protected boolean onStartSegment(Marker marker) {
		Marker newMarker = new Marker();
		newMarker.setLabel(marker.getLabel());
		newMarker.setStart(marker.getStart());
//		newMarker.setStartSampleNum(marker.getStartSampleNum());
		setCurrentMarker(newMarker);
		LOG.debug("[onSegmentStarted] {0}", newMarker.toString());
		LOG.debug("[onSegmentedStarted] marker: {0}; Markers: {1}", marker, getMarkSet());
		return true;
	}

	protected boolean onSegmentEnded(Marker marker) {
		if(marker == null ) return false;
		marker.setLength(marker.getLength());
		getMarkSet().getMarkers().add(marker);
		setCurrentMarker(null);
		LOG.debug("[onSegmentEnded] marker: {0}; Markers: {1}", marker, getMarkSet());
		return true;
	}
	

	protected LinkedHashMap<Long, Integer> getSequenceSignal() {
		if(sequenceSignal == null){
			sequenceSignal = new LinkedHashMap<Long, Integer>();
		}
		return sequenceSignal;
	}
	public LinkedHashMap<Long, Integer> getSequenceNoise() {
		if(sequenceNoise == null){
			sequenceNoise = new LinkedHashMap<Long, Integer>();
		}
		return sequenceNoise;
	}

	public void registered(String id) {
		classifiersCount++;
		classifiersThreshold = classifiersCount/2;
		LOG.debug("[registered] classifiersCount: {0}; classifiersThreshold: {1}", classifiersCount, classifiersThreshold);
	}

	protected Marker getCurrentMarker() {
		return currentMarker;
	}

	protected void setCurrentMarker(Marker marker) {
		this.currentMarker = marker;
	}

	public Integer getClassifiersCount() {
		return classifiersCount;
	}


	public Integer getClassifiersThreshold() {
		return classifiersThreshold;
	}

	public void setClassifiersThreshold(Integer classifiersThreshold) {
		this.classifiersThreshold = classifiersThreshold;
	}

	@Override
	public Collection<SignalSegment> getSignalSegments() {
		throw new IllegalArgumentException("Not implemented");
	}

	@Override
	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}

	public IExtractorConfig getConfig() {
		return config;
	}


}
