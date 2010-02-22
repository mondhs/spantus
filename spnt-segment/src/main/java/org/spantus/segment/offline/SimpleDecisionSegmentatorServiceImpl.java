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
/**
 * Simple rules for extraction more stables segmentation result.
 * Class applies rules on segmentation results from {@link #getSegmentator()}
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 1, 2010
 * 
 * @see MergeSegmentatorServiceImpl
 *
 */
public class SimpleDecisionSegmentatorServiceImpl extends AbstractSegmentatorService {

	protected Logger log = Logger.getLogger(getClass());

	private ISegmentatorService segmentator;
	/**
	 * process with some decision logic segments that are extracted by {@link #getSegmentator()}
	 */
	public MarkerSetHolder extractSegments(Set<IClassifier> classifiers,
			SegmentatorParam param) {
		//merge segments with segmentator
		MarkerSetHolder markerSetHolder = getSegmentator().extractSegments(classifiers,
				param);
		//init parameters
		MarkerSet phoneMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		MarkerSet markerSet = phoneMarkerSet.clone();
		markerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());

		//if word level no info but exists phone level, clone phone level
		if(markerSet == null && markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name())!= null){
			markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
			markerSet = markerSet.clone();
			markerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());
			markerSetHolder.getMarkerSets().put(markerSet.getMarkerSetType(), markerSet);
		}
		BaseDecisionSegmentatorParam safe_param = createSafeParam(param);
		//if there is no segments just return empty segmentaion results
		if (markerSet.getMarkers().size()==0) {
			return markerSetHolder;
		}
		//create working collection
		Map<Marker, MarkerDto> markerDtos = createDto(markerSet.getMarkers());
		
		//process the working collection with hard-coded rules
		process(markerDtos, safe_param);
		
		//extract data from working collection
		markerSet.getMarkers().clear();
		MarkerDto firstMarkerDto = markerDtos.values().iterator().next();
		MarkerDto currentDto = firstMarkerDto;
		markerSet.getMarkers().add(currentDto.getMarker());
		while (currentDto.getNext() != null) {
			currentDto = currentDto.getNext();
			markerSet.getMarkers().add(currentDto.getMarker());
		}
		log.debug("extractSegments: " + markerSet.getMarkers());
		markerSetHolder.getMarkerSets().put(markerSet.getMarkerSetType(), markerSet);
		return markerSetHolder;
	}
	
	/**
	 * process working collection, with simple logic. actions remove
	 * 
	 * @param markerDtos
	 * @param param
	 */
	protected void process(Map<Marker, MarkerDto> markerDtos,
			BaseDecisionSegmentatorParam param) {
		Set<Marker> removed = new LinkedHashSet<Marker>();
		log.debug("[process] with parameters {0}", param);
		//search for removal. 
		for (MarkerDto markerDto : markerDtos.values()) {
			if (isForRemove(markerDto.getMarker(), markerDto
					.getDistanceToPrevious(), markerDto.getDistanceToNext(),
					param)) {
				log.debug("[process] marking for delete: " + markerDto);

				markerDto.getPrevious().setNext(markerDto.getNext());
				markerDto.getNext().setPrevious(markerDto.getPrevious());
				removed.add(markerDto.getMarker());
			}
		}
		//remove marked for deletion
		for (Marker marker : removed) {
			markerDtos.remove(marker);
		}
		// log.debug("Remove noises: " + removed);
		removed.clear();
		//join action
		for (MarkerDto markerDto : markerDtos.values()) {
			log.debug("[process] minLength: {0}; distances [{1};{2}]",markerDto.getMarker().getLength(), markerDto.getDistanceToPrevious(), markerDto.getDistanceToNext());
			if (param.getMinLength()>markerDto.getMarker().getLength()) {
				if (markerDto.getDistanceToNext().intValue() > 0
						&& param.getMinSpace()>
								markerDto.getDistanceToNext()) {
					log.debug("[process] joint " + markerDto + "to next: "
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
					log.debug("[process] joint " + markerDto + "to previous: "
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
		log.debug("[process] Remove joined: " + removed);
		removed.clear();

		for (MarkerDto markerDto : markerDtos.values()) {
			if (markerDto.getDistanceToNext().intValue() > 0
					&& param.getMinSpace()>
							markerDto.getDistanceToNext()) {
				log.debug("[process] joint " + markerDto + "to next: "
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
		log.debug("[process] Remove joined: " + removed);
		removed.clear();

	}
	/**
	 * create dto {@link MarkerDto} for every marker
	 * 
	 * @param markers
	 * @return return the map with 
	 */
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
	/**
	 * is this segment possible to remove
	 * @param current
	 * @param distanceToPrevious
	 * @param distanceToNext
	 * @param param
	 * @return
	 */
	protected boolean isForRemove(Marker current,
			Long distanceToPrevious, Long distanceToNext,
			BaseDecisionSegmentatorParam param) {
		return current.getLength()<param.getMinLength()
				&& distanceToNext>param.getMinSpace()
				&& distanceToPrevious>param
						.getMinSpace();
	}
	/**
	 * 
	 * @param markerSet
	 * @param removed
	 */
	protected void remove(MarkerSet markerSet, Set<Marker> removed) {
		for (Marker marker : removed) {
			markerSet.getMarkers().remove(marker);
		}
	}
	/**
	 * 
	 * @param thresholds
	 * @return
	 */
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
	/**
	 * Some segmentation service to do the general extraction logic. {@link MergeSegmentatorServiceImpl}
	 * @return
	 */
	public ISegmentatorService getSegmentator() {
		return segmentator;
	}

	public void setSegmentator(ISegmentatorService segmentator) {
		this.segmentator = segmentator;
	}

}
