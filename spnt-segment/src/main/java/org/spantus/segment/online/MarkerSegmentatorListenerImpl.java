package org.spantus.segment.online;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;

public class MarkerSegmentatorListenerImpl extends
		MultipleSegmentatorListenerOnline {

	public static final String SIGNAL_WINDOWS = "SIGNAL_WINDOWS";

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(MarkerSegmentatorListenerImpl.class);
	
	private Map<String, Marker> currentMarkerMap = new HashMap<String, Marker>();

	
	private List<SignalSegment> signalSegments = new ArrayList<SignalSegment>();
	
	private SignalSegment currentSegment;
	
	private String firstFeatureId = null;;

	@Override
	public void onSegmentStarted(SegmentEvent event) {
		getCurrentMarkerMap().put(event.getExtractorId(), createMarker(event));
//		LOG.debug("[onSegmentedStarted] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
		if(getCurrentSegment() == null && getCurrentMarkerMap().size()>getClassifiersThreshold()){
			setCurrentSegment(createSegment(event));
//			LOG.debug("[onSegmentedStarted] +++ {0}: {1}", event, getCurrentSegment());
			
		}

	}



	@Override
	public void onSegmentEnded(SegmentEvent event) {
		
		currentMarkerMap.remove(event.getExtractorId());
//		LOG.debug("[onSegmentEnded] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());

		if(getCurrentSegment() != null && getCurrentMarkerMap().size()<=getClassifiersThreshold()){
			finazlizeSegment(getCurrentSegment(), event);
//			LOG.debug("[onSegmentEnded] --- {0}: {1}", event, getCurrentSegment());
			signalSegments.add(getCurrentSegment());
			processEndedSegment(getCurrentSegment());
			setCurrentSegment(null);
			
		}
		
		
	}
	
	protected void processEndedSegment(SignalSegment signalSegment) {
		//do nohting
	}



	private SignalSegment createSegment(SegmentEvent event) {
		SignalSegment signalSegment = new SignalSegment();
		signalSegment.setMarker(createMarker(event));
		signalSegment.setId(""+event.getTime());
		return signalSegment;
	}

	private void finazlizeSegment(SignalSegment aSegment,
			SegmentEvent event) {
		finazlizeMarker(aSegment.getMarker(), event);
	}

	@Override
	public void onSegmentProcessed(SegmentEvent event) {
		if(getCurrentSegment() != null){
			
			if(firstFeatureId == null){
				firstFeatureId = event.getExtractorId();
			}
			
			String extractorId = event.getExtractorId();
			FrameValuesHolder valueHolder = getCurrentSegment().getFeatureFrameValuesMap().get(extractorId);
			FrameVectorValuesHolder vectorHolder = getCurrentSegment().getFeatureFrameVectorValuesMap().get(SIGNAL_WINDOWS);
			if(valueHolder == null){
				valueHolder = new FrameValuesHolder();
				valueHolder.setValues(new FrameValues(event.getOutputValues().getSampleRate()));
				getCurrentSegment().getFeatureFrameValuesMap().put(extractorId, valueHolder);
			}
			if(vectorHolder == null){
				vectorHolder = new FrameVectorValuesHolder();
				vectorHolder.setValues(new FrameVectorValues(event.getOutputValues().getSampleRate()));
				getCurrentSegment().getFeatureFrameVectorValuesMap().put(SIGNAL_WINDOWS, vectorHolder);
			}
			valueHolder.getValues().add(event.getValue());
			
			if(event.getExtractorId().equals(firstFeatureId)){
				vectorHolder.getValues().add(event.getWindowValues());
			}
		}
	}
	@Override
	public void registered(String id) {
		super.registered(id);
	}

	public Map<String, Marker> getCurrentMarkerMap() {
		return currentMarkerMap;
	}

	public void setCurrentMarkerMap(Map<String, Marker> currentMarkerMap) {
		this.currentMarkerMap = currentMarkerMap;
	}
	
	@Override
	public Collection<SignalSegment> getSignalSegments() {
		return signalSegments;
	}

	public SignalSegment getCurrentSegment() {
		return currentSegment;
	}

	public void setCurrentSegment(SignalSegment currentSegment) {
		this.currentSegment = currentSegment;
	}



}
