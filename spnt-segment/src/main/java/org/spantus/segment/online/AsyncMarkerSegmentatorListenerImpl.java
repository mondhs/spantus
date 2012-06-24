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

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;

public class AsyncMarkerSegmentatorListenerImpl implements ISegmentatorListener {

	private static final Logger LOG = Logger
			.getLogger(AsyncMarkerSegmentatorListenerImpl.class);

	private Map<Long, Set<SegmentEvent>> syncMap = new TreeMap<Long, Set<SegmentEvent>>();
	private Map<Long, Set<SegmentEvent>> syncWindowValuesMap = new TreeMap<Long, Set<SegmentEvent>>();
	private Set<Long> syncTime = new TreeSet<Long>();
	private Long lastProcessedSample = -Long.MAX_VALUE;
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

		if (lastProcessedSample > theEvent.getSample()) {
			return;
		}

		Set<SegmentEvent> aSet = syncMap.get(theEvent.getSample());
		syncTime.add(theEvent.getSample());

		if (aSet == null) {
			aSet = new HashSet<SegmentEvent>();
			syncMap.put(theEvent.getSample(), aSet);
		}
		theEvent.setSignalState(true);
		aSet.add(theEvent);
	}

	/**
	 * 
	 */
	@Override
	public void onSegmentEnded(SegmentEvent theEvent) {

		if (lastProcessedSample > theEvent.getSample()) {
			return;
		}

		Set<SegmentEvent> aSet = syncMap.get(theEvent.getSample());
		syncTime.add(theEvent.getSample());
		if (aSet == null) {
			aSet = new HashSet<SegmentEvent>();
			syncMap.put(theEvent.getSample(), aSet);
		}
		theEvent.setSignalState(false);
		aSet.add(theEvent);

		Long aCleanupTime = null;
		// LOG.debug("[onSegmentEnded] on {0}[{1}] +++++++++++++++++++++++++++++++++++++++++",theEvent.getSample(),
		// theEvent.getExtractorId());
		for (Long iTime : syncTime) {
			if (syncMap.get(iTime) != null) {
				// LOG.debug("[onSegmentEnded] changes on {0}", iTime);
				Long curentCleanupTime = onChangePointAsync(iTime);
				if (curentCleanupTime != null) {
					aCleanupTime = curentCleanupTime;
					break;
				}
			} else if (syncWindowValuesMap.get(iTime) != null) {
				onSegmentProcessedAsync(iTime);
			}
		}
		// LOG.debug("[onSegmentEnded] on {0} [{1}] ------------------------------------------",
		// theEvent.getSample(), theEvent.getExtractorId());
		clearUsedValues(aCleanupTime);
	}

	/**
	 * 
	 * @param iTime
	 * @return
	 */
	private Long onChangePointAsync(Long iTime) {
		Long aCleanupTime = null;
		for (SegmentEvent iEvent : syncMap.get(iTime)) {
			if (iEvent.getSignalState()) {
				onSegmentStartedAsync(iEvent);
				onSegmentProcessedAsync(iTime);
			} else {
				onSegmentProcessedAsync(iTime);
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
		if (syncWindowValuesMap.get(iTime) != null) {
			for (SegmentEvent iEvent : syncWindowValuesMap.get(iTime)) {
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
		underlyingSegmentator.getSignalSegments().clear();
		underlyingSegmentator.getCurrentMarkerMap().clear();
		underlyingSegmentator.getMarkSet().getMarkers().clear();

		if (aTimeMomoment != null) {
			LOG.debug("[clearUsedValues]till: {0}", aTimeMomoment);
			lastProcessedSample = aTimeMomoment;
			for (Iterator<Long> iterator = syncTime.iterator(); iterator
					.hasNext();) {
				Long iTime = (Long) iterator.next();
				if (iTime <= aTimeMomoment) {
					iterator.remove();
				} else {
					break;
				}

			}
			for (Iterator<Entry<Long, Set<SegmentEvent>>> iterator = syncMap
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<Long, Set<SegmentEvent>> iTimeEvent = iterator.next();
				if (iTimeEvent.getKey() <= aTimeMomoment) {
					iterator.remove();
				} else {
					break;
				}

			}
			for (Iterator<Entry<Long, Set<SegmentEvent>>> iterator = syncWindowValuesMap
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<Long, Set<SegmentEvent>> iTimeEvent = iterator.next();
				if (iTimeEvent.getKey() <= aTimeMomoment) {
					iterator.remove();
				} else {
					break;
				}

			}
		}

	}

	public List<SignalSegment> getSignalSegments() {
		return signalSegments;
	}

	@Override
	public void onSegmentProcessed(SegmentEvent theEvent) {
		if (lastProcessedSample > theEvent.getSample()) {
			return;
		}
		Assert.isTrue(theEvent.getWindowValues() != null,
				"Window values cannot be null");
		syncTime.add(theEvent.getSample());
		Set<SegmentEvent> aSet = syncWindowValuesMap.get(theEvent.getSample());
		if (aSet == null) {
			aSet = new HashSet<SegmentEvent>();
			syncWindowValuesMap.put(theEvent.getSample(), aSet);
		}
		aSet.add(theEvent);

	}

	@Override
	public void onNoiseProcessed(SegmentEvent event) {
		// underlyingSegmentator.onNoiseProcessed(event);

	}

	@Override
	public void registered(String id) {
		classifiersCount++;
		classifiersThreshold = classifiersCount / 2;
		// LOG.debug("[registered] classifiersCount: {0}; classifiersThreshold: {1}",
		// classifiersCount, classifiersThreshold);
		underlyingSegmentator.registered(id);
	}

	@Override
	public void setConfig(IExtractorConfig config) {
		underlyingSegmentator.setConfig(config);
	}

}
