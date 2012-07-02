package org.spantus.segment.online;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.spantus.core.beans.SegmentChronology;

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;

public class AsyncMarkerSegmentatorListenerImpl implements ISegmentatorListener {

	private static final Logger LOG = Logger
			.getLogger(AsyncMarkerSegmentatorListenerImpl.class);

	private SegmentChronology<SegmentEvent> changePointChronology = new SegmentChronology<SegmentEvent>(10,2);
	private SegmentChronology<SegmentEvent> segmentValuesChronology = new SegmentChronology<SegmentEvent>(10,2);
//	private Set<Long> syncTime = new TreeSet<Long>();
//	private Long lastProcessedSample = -Long.MAX_VALUE;
	private List<SignalSegment> signalSegments = new ArrayList<SignalSegment>();
	private MarkerSegmentatorListenerImpl underlyingSegmentator;

	private int classifiersCount;

	@SuppressWarnings("unused")
	private int classifiersThreshold;

	public AsyncMarkerSegmentatorListenerImpl(
			MarkerSegmentatorListenerImpl underlyingSegmentator) {
		this.underlyingSegmentator = underlyingSegmentator;
	}

	@Override
	public void onSegmentStarted(SegmentEvent theEvent) {

//		if (changePointChronology.getStartMoment() > theEvent.getSample()) {
//			return;
//		}
                LOG.debug("[onSegmentStarted] on {0}[{1}]",theEvent.getSample(),
                    theEvent.getExtractorId());


		theEvent.setSignalState(true);
                changePointChronology.add(theEvent.getSample(), theEvent);
	}

	/**
	 * 
	 */
	@Override
	public void onSegmentEnded(SegmentEvent theEvent) {

//		if (changePointChronology.getStartMoment() > theEvent.getSample()) {
//			return;
//		}
		theEvent.setSignalState(false);
		changePointChronology.add(theEvent.getSample(), theEvent);

		Long aCleanupTime = changePointChronology.getLastFullBinIndex();
		LOG.debug("[onSegmentEnded] on {0}[{1}] +++++++++++++++++++++++++++++++++++++++++",theEvent.getSample(),
                    theEvent.getExtractorId());
		for (Entry<Long, Set<SegmentEvent>> bin : changePointChronology.getPrior(aCleanupTime)) {
                    Long iTime = bin.getKey();
                    if (!bin.getValue().isEmpty()) {
                            // LOG.debug("[onSegmentEnded] changes on {0}", iTime);
                             onChangePointAsync(bin);
                    } else if (segmentValuesChronology.get(iTime) != null) {
                            onSegmentProcessedAsync(iTime);
                    }
		}
		 LOG.debug("[onSegmentEnded] on {0} [{1}] ------------------------------------------",
		 theEvent.getSample(), theEvent.getExtractorId());
                 clearUsedValues(aCleanupTime);
	}

	/**
	 * 
	 * @param iTime
	 * @return
	 */
	private Long onChangePointAsync(Entry<Long, Set<SegmentEvent>> changePointBin) {
                LOG.debug("[onChangePointAsync] on {0}",changePointBin);
		Long aCleanupTime = null;
		for (SegmentEvent iEvent : changePointBin.getValue()) {
			if (iEvent.getSignalState()) {
				onSegmentStartedAsync(iEvent);
				onSegmentProcessedAsync(changePointBin.getKey());
			} else {
				onSegmentProcessedAsync(changePointBin.getKey());
				aCleanupTime = onSegmentEndedAsync(iEvent);
			}
		}
		return aCleanupTime;
	}

	/**
	 * 
	 * @param iEvent
	 */
	private void onSegmentStartedAsync(SegmentEvent iEvent) {
		underlyingSegmentator.onSegmentStarted(iEvent);
	}

	private void onSegmentProcessedAsync(Long iTime) {
		if (segmentValuesChronology.get(iTime) != null) {
			for (SegmentEvent iEvent : segmentValuesChronology.get(iTime)) {
				underlyingSegmentator.onSegmentProcessed(iEvent);
			}
		}
	}

	/**
	 * 
	 * @param iEvent
	 * @return
	 */
	private Long onSegmentEndedAsync(SegmentEvent iEvent) {
		underlyingSegmentator.onSegmentEnded(iEvent);
		Long aTimeMomoment = null;
		if (underlyingSegmentator.getSignalSegments().size() > 0) {
			LOG.debug("[onSegmentEnded] on {1} found: {0}; markers: {2}",
					underlyingSegmentator.getSignalSegments(),
					iEvent.getSample(),
					underlyingSegmentator.getSignalSegments());
			getSignalSegments().addAll(
					underlyingSegmentator.getSignalSegments());
			underlyingSegmentator.getSignalSegments().clear();
			aTimeMomoment = iEvent.getSample();
		}
		return aTimeMomoment;
	}

	private void clearUsedValues(Long aTimeMomoment) {
                if(aTimeMomoment == null){
                    return;
                }
                LOG.debug("[clearUsedValues]till: {0}", aTimeMomoment);
		underlyingSegmentator.getSignalSegments().clear();
		underlyingSegmentator.getCurrentMarkerMap().clear();
		underlyingSegmentator.getMarkSet().getMarkers().clear();
                changePointChronology.cleanUpTill(aTimeMomoment);
                segmentValuesChronology.cleanUpTill(aTimeMomoment);
	}

        @Override
	public List<SignalSegment> getSignalSegments() {
		return signalSegments;
	}

	@Override
	public void onSegmentProcessed(SegmentEvent theEvent) {
		if (changePointChronology.getStartMoment() > theEvent.getSample()) {
			return;
		}
		Assert.isTrue(theEvent.getWindowValues() != null,
				"Window values cannot be null");
                segmentValuesChronology.add(theEvent.getSample(), theEvent);

	}

	@Override
	public void onNoiseProcessed(SegmentEvent event) {
		// underlyingSegmentator.onNoiseProcessed(event);

	}

	@Override
	public void registered(String id) {
		classifiersCount++;
		classifiersThreshold = (classifiersCount / 2)+(classifiersCount % 2);
		// LOG.debug("[registered] classifiersCount: {0}; classifiersThreshold: {1}",
		// classifiersCount, classifiersThreshold);
		underlyingSegmentator.registered(id);
                segmentValuesChronology.setFullThreshold(classifiersThreshold);
                changePointChronology.setFullThreshold(classifiersThreshold);
	}

	@Override
	public void setConfig(IExtractorConfig config) {
		underlyingSegmentator.setConfig(config);
	}

}
