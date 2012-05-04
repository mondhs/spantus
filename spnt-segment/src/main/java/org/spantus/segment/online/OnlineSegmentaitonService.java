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

import java.util.Collection;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;
import org.spantus.segment.SegmentatorParam;
import org.spantus.segment.offline.BasicSegmentatorServiceImpl;
/**
 * Online segmentation algorithm
 * 
 * @author Mindaugas Greibus
 *
 */
public class OnlineSegmentaitonService extends BasicSegmentatorServiceImpl {
	private Logger log = Logger.getLogger(getClass());
	
	public OnlineSegmentaitonService() {
	}
	
	/**
	 * 
	 */
	public MarkerSetHolder extractSegments(Collection<IClassifier> classifiers,
			SegmentatorParam param) {

		DecisionSegmentatorOnline multipleSegmentator = new DecisionSegmentatorOnline();
		if(param != null){
			multipleSegmentator.setParam((OnlineDecisionSegmentatorParam)param);
		}
		MarkerSetHolder mergedHolder = super.extractSegments(classifiers, param);
		MarkerSet markerSet = mergedHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		
		//if word level no info but exists phone level, clone phone level
		if(markerSet == null && mergedHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name())!= null){
			markerSet = mergedHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
			markerSet = markerSet.clone();
			markerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());
			mergedHolder.getMarkerSets().put(markerSet.getMarkerSetType(), markerSet);
		}

		
		long index = 0;
		int resolution = 1;
		String id= "1";
		for (Marker marker : markerSet.getMarkers()) {
			for (; index < marker.getStart(); index += resolution) {
				SegmentEvent event = new SegmentEvent(); 
				event.setExtractorId(id);
				event.setSample(index);
				event.setTime(index);
				multipleSegmentator.noiseDetected(event);
			}
			for (; index <= marker.getEnd(); index += resolution) {
				SegmentEvent event = new SegmentEvent();
				event.setExtractorId(id);
				event.setSample(index);
				event.setTime(index);
				multipleSegmentator.segmentDetected(event);
			}
//			log.debug("marker:{0}; stateSum{1}",marker,statesSums);
		}
		for (int i = 0; i <= multipleSegmentator.getParam().getMinSpace(); i++) {
			index++;
			SegmentEvent event = new SegmentEvent();
			event.setExtractorId(id);
			event.setSample(index);
			event.setTime(index);
			multipleSegmentator.noiseDetected(event);
		}
		index++;
		SegmentEvent event = new SegmentEvent();
		event.setExtractorId(id);
		event.setSample(index);
		event.setTime(index);
		multipleSegmentator.noiseDetected(event);
		
		
		
		
		int num = 1;
		for (Marker marker : multipleSegmentator.getMarkSet().getMarkers()) {
			marker.setLabel("" +(num++));
		}
		
		
		log.debug("[extractSegments] {0}", multipleSegmentator.getMarkSet().getMarkers());
		
//		MarkerSetHolder holder = new MarkerSetHolder();
		mergedHolder.getMarkerSets().put(multipleSegmentator.getMarkSet().getMarkerSetType(), multipleSegmentator.getMarkSet());
		
		return mergedHolder;
	}

//	@Override
//	protected void safeSum(Map<Long, Float> statesSums, Long time, Float value,
//			SegmentatorParam param, IClassifier classifier) {
//		super.safeSum(statesSums, time, value, param, classifier);
////		multipleSegmentator.processState(time, ((AbstractClassifier)classifier).getExtractor(), value);
//	}

}
