/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.segment.online;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
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

public class MultipleSegmentatorOnline implements OnlineSegmentator {

	private MarkerSet markerSet;

	private Marker currentMarker;

	private Logger log = Logger.getLogger(MultipleSegmentatorOnline.class);

	private Set<IGeneralExtractor> extractors;
	private Map<Long, Map<IGeneralExtractor, Float>> status;
	private Boolean lastState = null;

	public void processState(Long sample, IGeneralExtractor extractor,
			Float val) {

		Long time = calculateTime(extractor, sample);
		Boolean state = getVoteForState(time, extractor, val);

		if (Boolean.TRUE.equals(state)) {
			if (!state.equals(lastState)) {
				onStartSegment(createSegment(sample, time));
				lastState = state;
			}
		} else if (Boolean.FALSE.equals(state)) {
			if (!state.equals(lastState)) {
				onSegmentEnded(finazlizeSegment(getCurrentMarker(), sample, time));
				lastState = state;
			}
		}else{
			// on null value, state is not clear.
			// waiting till more extractors calculate current state
		}
	}

	protected Long calculateTime(IGeneralExtractor extractor, Long sample) {
		BigDecimal time = new BigDecimal(sample.floatValue()
				/ 
				(extractor.getConfig().getSampleRate()/1000)
				).setScale(0, RoundingMode.HALF_UP);
//		time = time.multiply(BigDecimal.valueOf(1000)).setScale(0,
//				RoundingMode.HALF_UP);
		return time.longValue();
	}

	protected Marker createSegment(Long sample,
			Long time) {
		Marker marker = new Marker();
		marker.setStart(time);
		marker.setLabel(time.toString());
//		log.debug("[createSegment] marker({0}ms): {1}", time, marker.toString());
		return marker;
	}

	protected Marker finazlizeSegment(Marker marker, Long sample, 
			Long time) {
		if (marker == null)
			return marker;
		marker.setEnd(time);
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
		if(marker == null || getCurrentMarker() == null) return false;
		
		marker.setLength(marker.getLength());
		getMarkSet().getMarkers().add(marker);
		log.debug("[onSegmentEnded] {0}", marker);
		setCurrentMarker(null);
		return true;
	}


	protected Marker getCurrentMarker() {
		return currentMarker;
	}

	public void setCurrentMarker(Marker currentMarker) {
		this.currentMarker = currentMarker;
	}

	public MarkerSet getMarkSet() {
		if (markerSet == null) {
			markerSet = new MarkerSet();
		}
		return markerSet;
	}

	public Set<IGeneralExtractor> getExtractors() {
		if (extractors == null) {
			extractors = new LinkedHashSet<IGeneralExtractor>();
		}
		return extractors;
	}
	/**
	 * Voting for state. 
	 * 
	 * @param time
	 * @param extractor
	 * @param f
	 * @return null not enough data for voting. true - signal. false - noise
	 */
	public Boolean getVoteForState(Long time, IGeneralExtractor extractor,
			Float f) {
		Map<IGeneralExtractor, Float> statusExtrs = getStatus().get(time);

		if (statusExtrs == null) {
			statusExtrs = new HashMap<IGeneralExtractor, Float>();
			getStatus().put(time, statusExtrs);
		}
		statusExtrs.put(extractor, f);
		getExtractors().add(extractor);
		if (getExtractors().size() == statusExtrs.size()) {
			Float sum = Float.valueOf(0f);
			for (Entry<IGeneralExtractor, Float> vals : statusExtrs.entrySet()) {
				sum += vals.getValue();
			}

			if ((sum / statusExtrs.size()) > .5) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
		return null;
	}

	public Map<Long, Map<IGeneralExtractor, Float>> getStatus() {
		if (status == null) {
			status = new LinkedHashMap<Long, Map<IGeneralExtractor, Float>>();
		}
		return status;
	}

}
