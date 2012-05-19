package org.spantus.segment.online;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;

public class AsyncMarkerSegmentatorListenerImpl implements ISegmentatorListener{
	
	private static final Logger LOG = Logger
			.getLogger(AsyncMarkerSegmentatorListenerImpl.class);

	private Map<Long, Set<SegmentEvent>> syncMap = new TreeMap<Long, Set<SegmentEvent>>();
	private List<SignalSegment> signalSegments = new ArrayList<SignalSegment>();
	private MarkerSegmentatorListenerImpl underlyingSegmentator;

	public AsyncMarkerSegmentatorListenerImpl(MarkerSegmentatorListenerImpl underlyingSegmentator) {
		this.underlyingSegmentator = underlyingSegmentator;
	}
	

	@Override
	public void onSegmentStarted(SegmentEvent event) {

		Set<SegmentEvent> aSet= syncMap.get(event.getSample());

		
		if (aSet == null) {
			aSet = new HashSet<SegmentEvent>();
			syncMap.put(event.getSample(), aSet);
		}
		event.setSignalState(true);
		aSet.add(event);
	}

	@Override
	public void onSegmentEnded(SegmentEvent theEvent) {
		Set<SegmentEvent> aSet = syncMap.get(theEvent.getSample());
		if (aSet == null) {
			aSet = new HashSet<SegmentEvent>();
			syncMap.put(theEvent.getSample(), aSet);
		}
		theEvent.setSignalState(false);
		aSet.add(theEvent);
		underlyingSegmentator.getSignalSegments().clear();
		underlyingSegmentator.getCurrentMarkerMap().clear();
		Long aTimeMomoment = null;
		for (Entry<Long, Set<SegmentEvent>> iTimeEvent : syncMap.entrySet()) {
			for (SegmentEvent iEvent : iTimeEvent.getValue()) {
				if(iEvent.getSignalState()){
					underlyingSegmentator.onSegmentStarted(iEvent);
				}else{
					underlyingSegmentator.onSegmentEnded(iEvent);
					if(underlyingSegmentator.getSignalSegments().size()>0){
						LOG.debug("[onSegmentEnded] on {1} found: {0}", underlyingSegmentator.getSignalSegments(), theEvent.getSample());
						getSignalSegments().addAll(underlyingSegmentator.getSignalSegments());
						underlyingSegmentator.getSignalSegments().clear();
						aTimeMomoment = iTimeEvent.getKey();
					}
				}
			}
		}
		clearUsedValues(aTimeMomoment);
	}
	

	private void clearUsedValues(Long aTimeMomoment) {
		LOG.debug("[clearUsedValues]till: {0}", aTimeMomoment);
		if(aTimeMomoment != null){
			for (Iterator<Entry<Long, Set<SegmentEvent>>> iterator = syncMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<Long, Set<SegmentEvent>> iTimeEvent = iterator.next();
				if(iTimeEvent.getKey()<aTimeMomoment){
					iterator.remove();
				}
				
			}
		}		
	}

	public List<SignalSegment> getSignalSegments() {
		return signalSegments;
	}


	@Override
	public void onSegmentProcessed(SegmentEvent event) {
		underlyingSegmentator.onSegmentProcessed(event);
		
	}


	@Override
	public void onNoiseProcessed(SegmentEvent event) {
		underlyingSegmentator.onNoiseProcessed(event);
		
	}


	@Override
	public void registered(String id) {
		underlyingSegmentator.registered(id);
	}


	@Override
	public void setConfig(IExtractorConfig config) {
		underlyingSegmentator.setConfig(config);
	}

}
