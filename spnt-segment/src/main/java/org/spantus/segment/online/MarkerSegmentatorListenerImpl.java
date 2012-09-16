package org.spantus.segment.online;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class MarkerSegmentatorListenerImpl extends
		MultipleSegmentatorListenerOnline {

//	public static final String SIGNAL_WINDOWS = "SIGNAL_WINDOWS";

	private static final Logger LOG = Logger
			.getLogger(MarkerSegmentatorListenerImpl.class);
	
	private Map<String, Marker> currentMarkerMap = new HashMap<String, Marker>();

	
	private List<SignalSegment> signalSegments = new ArrayList<SignalSegment>();
	
	private SignalSegment currentSegment;
	
	private String firstFeatureId = null;
        
        private Long lastEvent;

	@Override
	public void onSegmentStarted(SegmentEvent event) {
                LOG.debug("[onSegmentStarted] +++ {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
                updateLastEvent(event.getTime());
		getCurrentMarkerMap().put(event.getExtractorId(), createMarker(event));
		if(getCurrentSegment() == null && getCurrentMarkerMap().size()>getClassifiersThreshold()){
//			LOG.debug("[onSegmentStarted] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
			setCurrentSegment(createSegment(event));
			LOG.debug("[onSegmentStarted] +++ {0}: {1}", event, getCurrentSegment());
			
		}

	}



	@Override
	public void onSegmentEnded(SegmentEvent event) {
                LOG.debug("[onSegmentEnded] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
                updateLastEvent(event.getTime());
		currentMarkerMap.remove(event.getExtractorId());
		if(getCurrentSegment() != null && ( getCurrentMarkerMap().size()<=getClassifiersThreshold() ||  getClassifiersThreshold()==0)){
//			LOG.debug("[onSegmentEnded] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
			finazlizeSegment(getCurrentSegment(), event);
			LOG.debug("[onSegmentEnded] --- {0}: {1}", event, getCurrentSegment());
			if(processEndedSegment(getCurrentSegment())){
				signalSegments.add(getCurrentSegment());
			}
			setCurrentSegment(null);
			
		}
		
		
	}
	
	protected boolean processEndedSegment(SignalSegment signalSegment) {
		//do nohting
		return true;
	}



	protected SignalSegment createSegment(SegmentEvent event) {
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
//                LOG.debug("[onSegmentProcessed] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
		Assert.isTrue(event.getWindowValues()!=null, "Event without windows values");
		if(getCurrentSegment() != null){
			
			if(firstFeatureId == null){
				firstFeatureId = event.getExtractorId();
			}
			
			String extractorId = event.getExtractorId();
			FrameValuesHolder valueHolder = getCurrentSegment().getFeatureFrameValuesMap().get(extractorId);
//			FrameVectorValuesHolder vectorHolder = getCurrentSegment().getFeatureFrameVectorValuesMap().get(SIGNAL_WINDOWS);
			if(valueHolder == null){
				valueHolder = new FrameValuesHolder();
				valueHolder.setValues(new FrameValues(event.getOutputValues().getSampleRate()));
				getCurrentSegment().getFeatureFrameValuesMap().put(extractorId, valueHolder);
			}
//			if(vectorHolder == null){
//				vectorHolder = new FrameVectorValuesHolder();
//				vectorHolder.setValues(new FrameVectorValues(event.getOutputValues().getSampleRate()));
//            			getCurrentSegment().getFeatureFrameVectorValuesMap().put(SIGNAL_WINDOWS, vectorHolder);
//			}
			valueHolder.getValues().add(event.getValue());
//			LOG.debug("[onSegmentProcessed] event.getValue(){1}?={2}{0} ", event.getValue(), event.getExtractorId(), firstFeatureId);
//			if(event.getExtractorId().equals(firstFeatureId)){
//				vectorHolder.getValues().add(event.getWindowValues());
//                               //LOG.debug("[onSegmentProcessed] ++++event.getWindowValues(){0} ", event.getWindowValues());
//			}
		}
	}
        
        public void onNoiseProcessed(SegmentEvent event) {
             LOG.debug("[onNoiseProcessed] {0} ({1} of {2})", event, getCurrentMarkerMap().size(), getClassifiersThreshold());
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

        private void updateLastEvent(Long time) {
             LOG.debug("[updateLastEvent] on {0} > [{1}] ",
		 lastEvent, time);
            if(lastEvent != null){
              Assert.isTrue(lastEvent <= time, "event is not happened in chrnological order");
//               LOG.error("event is not happened in chrnological order");
            }
            lastEvent = time;
        }
        




}
