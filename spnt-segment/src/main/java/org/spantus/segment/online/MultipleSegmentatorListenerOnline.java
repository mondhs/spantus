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

import java.util.LinkedHashMap;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;

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
	private Logger log = Logger
			.getLogger(MultipleSegmentatorListenerOnline.class);

	private LinkedHashMap<Long, Integer> sequenceSignal;  
	private LinkedHashMap<Long, Integer> sequenceNoise;
	private Integer classifiersCount = 0; 
	private Integer classifiersThreshold = 0;
	private Marker currentMarker;
	private MarkerSet markerSet;
	
	public MarkerSet getMarkSet() {
		if(markerSet == null){
			markerSet = new MarkerSet(); 
			markerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());
		}
		return markerSet;
	}

	public void onSegmentedStarted(SegmentEvent event) {
//		log.debug("[onSegmentedStarted] {0}", event);
	}

	public void onSegmentedEnded(SegmentEvent event) {
//		log.debug("[onSegmentedStarted] {0}", event);

	}

	public void onSegmentedProcessed(SegmentEvent event) {
//		log.debug("[onSegmentedProcessed] {0}", event);
		SegmentEvent multievent = event.clone();
		multievent.setMarker(null);
		Long time = event.getTime();
		Integer singnalCount = getSequenceSignal().get(time);
		Integer noiseCount = getSequenceNoise().get(time);
		if(singnalCount==null){
			singnalCount = 0;
			noiseCount = 0;
			getSequenceSignal().put(time, singnalCount);
			getSequenceNoise().put(time, noiseCount);
		}
		if (event.getMarker() == null) {
			getSequenceNoise().put(time,++noiseCount );
		} else {
			getSequenceSignal().put(time,++singnalCount );
		}
		Integer totalCount =  singnalCount+noiseCount;
		if(totalCount == classifiersCount){
			if(noiseCount>classifiersThreshold){
				if(getCurrentMarker() != null){
					getCurrentMarker().setEnd(time);
					multievent.setMarker(getCurrentMarker());
					setCurrentMarker(null);
				}
				noiseDetected(multievent);
			}else if(singnalCount>classifiersThreshold){
				if(getCurrentMarker() == null){
					setCurrentMarker(createSegment(event));
					multievent.setMarker(getCurrentMarker());
				}
				segmentDetected(multievent);
			}else {
				throw new IllegalArgumentException("Not impl");
//				multievent.setMarker(getCurrentMarker());
//				segmentProcessedMulti(multievent);
				
			}
							
		}
	}
	protected void segmentDetected(SegmentEvent event){
//		log.debug("[segmentStartedMulti] started {0} on {1}ms", currentMarker, event.getTime());
	}
	protected void noiseDetected(SegmentEvent event){
//		log.debug("[segmentEndedMulti] ended {0} on {1}ms", currentMarker, event.getTime());
		onSegmentEnded(event.getMarker());
	}
//	protected void segmentProcessedMulti(SegmentEvent event){
//		log.debug("[segmentProcessedMulti] processed {0} on {1}ms", currentMarker, event.getTime());
//	}

	
	protected Marker createSegment(SegmentEvent event) {
		Marker marker = new Marker();
		marker.setStart(event.getTime());
		marker.setLabel(""+event.getTime());
//		log.debug("[createSegment] marker({0}ms): {1}", time, marker.toString());
		return marker;
	}
	protected Marker finazlizeSegment(Marker marker, SegmentEvent event) {
		if (marker == null)
			return marker;
		marker.setEnd(event.getTime());
//		log.debug("[finazlizeSegment] marker({0}ms): {1}", time, marker.toString());
		return marker;
	}
	
	protected boolean onStartSegment(Marker marker) {
		Marker newMarker = new Marker();
		newMarker.setLabel(marker.getLabel());
		newMarker.setStart(marker.getStart());
//		newMarker.setStartSampleNum(marker.getStartSampleNum());
		setCurrentMarker(newMarker);
//		log.debug("[onSegmentStarted] {0}", newMarker.toString());
		return true;
	}
	protected boolean onSegmentEnded(Marker marker) {
		if(marker == null ) return false;
		
		marker.setLength(marker.getLength());
		getMarkSet().getMarkers().add(marker);
		log.debug("[onSegmentEnded] {0}", marker);
		setCurrentMarker(null);
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
	}

	protected Marker getCurrentMarker() {
		return currentMarker;
	}

	protected void setCurrentMarker(Marker marker) {
		this.currentMarker = marker;
	}

	

	

}
