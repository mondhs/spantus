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

import java.util.Set;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.AbstractSegmentatorService;
import org.spantus.segment.SegmentatorParam;
/**
 * Online segmentation algorithm
 * 
 * @author Mindaugas Greibus
 *
 */
public class OnlineSegmentaitonService extends AbstractSegmentatorService {
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 
	 */
	public MarkerSetHolder extractSegments(Set<IClassifier> thresholds,
			SegmentatorParam param) {

		DecisionSegmentatorOnline multipleSegmentator = new DecisionSegmentatorOnline();
		if(param != null){
			multipleSegmentator.setParam((OnlineDecisionSegmentatorParam)param);
		}
//		Map<IClassifier, Iterator<Float>> thresholdMap = new HashMap<IClassifier, Iterator<Float>>();
//		Integer delta = null;
		
//		for (IClassifier threshold : thresholds) {
//			thresholdMap.put(threshold, threshold.getState().iterator());
//			if(delta == null){
//				delta = threshold.getConfig().getWindowOverlap();
//			}else{
//				Assert.isTrue(delta==threshold.getConfig().getWindowOverlap());
//			}
//		}
		
//		Long i = delta.longValue();
//		boolean hasMore = true;
//		while(hasMore){
//			hasMore = false;
//			for (Entry<IClassifier, Iterator<Float>> thresholdEntry : thresholdMap.entrySet()) {
//				if(thresholdEntry.getValue().hasNext()){
//					multipleSegmentator.processState(i, thresholdEntry.getKey(), thresholdEntry.getValue().next());
//					hasMore = true;
//				}
//					
//			}
//			i+=delta;			
//		}
		int num = 1;
		for (Marker marker : multipleSegmentator.getMarkSet().getMarkers()) {
			marker.setLabel("" +(num++));
		}
		
		
		log.debug("[extractSegments] {0}", multipleSegmentator.getMarkSet().getMarkers());
		
		MarkerSetHolder holder = new MarkerSetHolder();
		holder.getMarkerSets().put(multipleSegmentator.getMarkSet().getMarkerSetType(), multipleSegmentator.getMarkSet());
		
		return holder;
	}
}
