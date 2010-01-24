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
package org.spantus.segment.offline;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.AbstractSegmentatorService;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentatorParam;
import org.spantus.utils.Assert;

public class SimpleDecisionSegmentatorServiceImpl extends AbstractSegmentatorService {

	protected Logger log = Logger.getLogger(getClass());

	private ISegmentatorService segmentator;
	/**
	 * 
	 */
	public MarkerSetHolder extractSegments(Set<IClassifier> classifiers,
			SegmentatorParam param) {
		MarkerSetHolder markerSetHolder = getSegmentator().extractSegments(classifiers,
				param);
		MarkerSet markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		SimpleDecisionSegmentatorParam _param = createParam(param);

		Iterator<Marker> markerIterator = markerSet.getMarkers().iterator();
		if (!markerIterator.hasNext()) {
			return markerSetHolder;
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
		return markerSetHolder;
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
			if (param.getMinLength()>markerDto.getMarker().getLength()) {
				if (markerDto.getDistanceToNext().intValue() > 0
						&& param.getMinSpace()>
								markerDto.getDistanceToNext()) {
					log.debug("joint " + markerDto + "to next: "
							+ markerDto.getNext());
					Marker current = markerDto.getMarker();
					Marker next = markerDto.getNext().getMarker();
					Long end = next.getStart()+next.getLength();
					// expand next marker
					next.setStart(current.getStart());
					next.setLength(end-current.getStart());
					// remove joined marker
					if (markerDto.getPrevious() != null) {
						markerDto.getPrevious().setNext(markerDto.getNext());
					}
					markerDto.getNext().setPrevious(markerDto.getPrevious());
					removed.add(markerDto.getMarker());
				} else if (markerDto.getDistanceToPrevious() > 0
						&& param.getMinSpace()>
								markerDto.getDistanceToPrevious()) {
					log.debug("joint " + markerDto + "to previous: "
							+ markerDto.getPrevious());
					Marker current = markerDto.getMarker();
					Marker previous = markerDto.getPrevious().getMarker();
					Long end = current.getStart()+current.getLength();
					// expand next marker
					previous.setLength(end-previous.getStart());
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
					&& param.getMinSpace()>
							markerDto.getDistanceToNext()) {
				log.debug("joint " + markerDto + "to next: "
						+ markerDto.getNext());
				Marker current = markerDto.getMarker();
				Marker next = markerDto.getNext().getMarker();
				Long end = next.getStart()+next.getLength();
				// expand next marker
				next.setStart(current.getStart());
				next.setLength(end-current.getStart());
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
			Long distanceToPrevious, Long distanceToNext,
			SimpleDecisionSegmentatorParam param) {
		return current.getLength()<param.getMinLength()
				&& distanceToNext>param.getMinSpace()
				&& distanceToPrevious>param
						.getMinSpace();
	}

	protected void remove(MarkerSet markerSet, Set<Marker> removed) {
		for (Marker marker : removed) {
			markerSet.getMarkers().remove(marker);
		}
	}

	protected float getSampleRate(Set<IClassifier> thresholds) {
		Float sampleRate = null;
		for (IClassifier threshold : thresholds) {
			Assert.isTrue(threshold.getExtractorSampleRate() > 0);
			if (sampleRate == null) {
				sampleRate = threshold.getExtractorSampleRate();
			} else {
				// should be same for all threshold states
				Assert.isTrue(sampleRate.equals(threshold.getExtractorSampleRate()));
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
