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
package org.spantus.segment.offline;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IThreshold;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentatorParam;
import org.spantus.utils.Assert;

public class SimpleDecisionSegmentatorServiceImpl implements
		ISegmentatorService {

	protected Logger log = Logger.getLogger(getClass());

	private ISegmentatorService segmentator;

	public MarkerSet extractSegments(Set<IThreshold> thresholds) {
		return extractSegments(thresholds, null);
	}

	public MarkerSet extractSegments(Set<IThreshold> thresholds,
			SegmentatorParam param) {
		MarkerSet markerSet = getSegmentator().extractSegments(thresholds,
				param);
		SimpleDecisionSegmentatorParam _param = createParam(param);

		Iterator<Marker> markerIterator = markerSet.getMarkers().iterator();
		if (!markerIterator.hasNext()) {
			return markerSet;
		}
		Map<Marker, MarkerDto> markerDtos = createDto(markerSet.getMarkers());
		process(markerDtos, _param);
		MarkerDto firstMarkerDto = markerDtos.values().iterator().next();
		markerSet.getMarkers().clear();
		MarkerDto currentDto = firstMarkerDto;
		markerSet.getMarkers().add(currentDto.getMarker());
		while (currentDto.getNext() != null) {
			currentDto = currentDto.getNext();
			markerSet.getMarkers().add(currentDto.getMarker());
		}
		log.debug("extractSegments: " + markerSet.getMarkers());
		return markerSet;
	}

	SimpleDecisionSegmentatorParam createParam(SegmentatorParam param) {
		if (param != null && param instanceof SimpleDecisionSegmentatorParam) {
			return (SimpleDecisionSegmentatorParam) param;
		}
		return new SimpleDecisionSegmentatorParam();

	}

	protected void process(Map<Marker, MarkerDto> markerDtos,
			SimpleDecisionSegmentatorParam param) {
		Set<Marker> removed = new LinkedHashSet<Marker>();
		for (MarkerDto markerDto : markerDtos.values()) {
			if (isForRemove(markerDto.getMarker(), markerDto
					.getDistanceToPrevious(), markerDto.getDistanceToNext(),
					param)) {
				log.debug("marking for delete: " + markerDto);

				markerDto.getPrevious().setNext(markerDto.getNext());
				markerDto.getNext().setPrevious(markerDto.getPrevious());
				removed.add(markerDto.getMarker());
			}
		}
		for (Marker marker : removed) {
			markerDtos.remove(marker);
		}
		// log.debug("Remove noises: " + removed);
		removed.clear();

		for (MarkerDto markerDto : markerDtos.values()) {
			if (param.getSegmentLengthThreshold().compareTo(
					markerDto.getMarker().getLength()) > 0) {
				if (markerDto.getDistanceToNext().intValue() > 0
						&& param.getSegmentsSpaceThreshold().compareTo(
								markerDto.getDistanceToNext()) > 0) {
					log.debug("joint " + markerDto + "to next: "
							+ markerDto.getNext());
					Marker current = markerDto.getMarker();
					Marker next = markerDto.getNext().getMarker();
					BigDecimal end = next.getStart().add(next.getLength());
					// expand next marker
					next.setStart(current.getStart());
					next.setLength(end.add(current.getStart().negate()));
					// remove joined marker
					if (markerDto.getPrevious() != null) {
						markerDto.getPrevious().setNext(markerDto.getNext());
					}
					markerDto.getNext().setPrevious(markerDto.getPrevious());
					removed.add(markerDto.getMarker());
				} else if (markerDto.getDistanceToPrevious().intValue() > 0
						&& param.getSegmentsSpaceThreshold().compareTo(
								markerDto.getDistanceToPrevious()) > 0) {
					log.debug("joint " + markerDto + "to previous: "
							+ markerDto.getPrevious());
					Marker current = markerDto.getMarker();
					Marker previous = markerDto.getPrevious().getMarker();
					BigDecimal end = current.getStart()
							.add(current.getLength());
					// expand next marker
					previous.setLength(end.add(previous.getStart().negate()));
					// remove joined marker
					if (markerDto.getNext() != null) {
						markerDto.getNext()
								.setPrevious(markerDto.getPrevious());
					}
					markerDto.getPrevious().setNext(markerDto.getNext());
					removed.add(markerDto.getMarker());
				}
			}
		}
		for (Marker marker : removed) {
			markerDtos.remove(marker);
		}
		log.debug("Remove joined: " + removed);
		removed.clear();

		for (MarkerDto markerDto : markerDtos.values()) {
			if (markerDto.getDistanceToNext().intValue() > 0
					&& param.getSegmentsSpaceThreshold().compareTo(
							markerDto.getDistanceToNext()) > 0) {
				log.debug("joint " + markerDto + "to next: "
						+ markerDto.getNext());
				Marker current = markerDto.getMarker();
				Marker next = markerDto.getNext().getMarker();
				BigDecimal end = next.getStart().add(next.getLength());
				// expand next marker
				next.setStart(current.getStart());
				next.setLength(end.add(current.getStart().negate()));
				// remove joined marker
				if (markerDto.getPrevious() != null) {
					markerDto.getPrevious().setNext(markerDto.getNext());
				}
				markerDto.getNext().setPrevious(markerDto.getPrevious());
				removed.add(markerDto.getMarker());
			}
		}
		for (Marker marker : removed) {
			markerDtos.remove(marker);
		}
		log.debug("Remove joined: " + removed);
		removed.clear();

	}

	public Map<Marker, MarkerDto> createDto(List<Marker> markers) {
		Map<Marker, MarkerDto> markerDto = new LinkedHashMap<Marker, MarkerDto>();
		MarkerDto previous = null;
		for (Marker marker : markers) {
			MarkerDto dto = new MarkerDto();
			dto.setMarker(marker);
			if (previous != null) {
				dto.setPrevious(previous);
				previous.setNext(dto);
			}
			markerDto.put(marker, dto);
			previous = dto;
		}
		return markerDto;
	}

	protected boolean isForRemove(Marker current,
			BigDecimal distanceToPrevious, BigDecimal distanceToNext,
			SimpleDecisionSegmentatorParam param) {
		return current.getLength().compareTo(param.getSegmentLengthThreshold()) < 0
				&& distanceToNext.compareTo(param.getSegmentsSpaceThreshold()) > 0
				&& distanceToPrevious.compareTo(param
						.getSegmentsSpaceThreshold()) > 0;
	}

	protected void remove(MarkerSet markerSet, Set<Marker> removed) {
		for (Marker marker : removed) {
			markerSet.getMarkers().remove(marker);
		}
	}

	protected float getSampleRate(Set<IThreshold> thresholds) {
		Float sampleRate = null;
		for (IThreshold threshold : thresholds) {
			Assert.isTrue(threshold.getState().getSampleRate() > 0);
			if (sampleRate == null) {
				sampleRate = threshold.getState().getSampleRate();
			} else {
				// should be same for all threshold states
				Assert.isTrue(sampleRate.equals(threshold.getState()
						.getSampleRate()));
			}
		}
		return sampleRate;
	}

	public ISegmentatorService getSegmentator() {
		return segmentator;
	}

	public void setSegmentator(ISegmentatorService segmentator) {
		this.segmentator = segmentator;
	}

}
