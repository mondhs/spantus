package org.spantus.segment.online;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IThreshold;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentatorParam;
import org.spantus.utils.Assert;

public class OnlineSegmentaitonService implements ISegmentatorService {
	private Logger log = Logger.getLogger(getClass());

	public MarkerSet extractSegments(Set<IThreshold> thresholds,
			SegmentatorParam param) {

		DecisionSegmentatorOnline multipleSegmentator = new DecisionSegmentatorOnline();
		if(param != null){
			multipleSegmentator.setParam((OnlineDecisionSegmentatorParam)param);
		}
		Map<IThreshold, Iterator<Float>> thresholdMap = new HashMap<IThreshold, Iterator<Float>>();
		Integer delta = null;
		
		for (IThreshold threshold : thresholds) {
			thresholdMap.put(threshold, threshold.getState().iterator());
			if(delta == null){
				delta = threshold.getConfig().getWindowOverlap();
			}else{
				Assert.isTrue(delta==threshold.getConfig().getWindowOverlap());
			}
		}
		
		Long i = delta.longValue();
		boolean hasMore = true;
		while(hasMore){
			hasMore = false;
			for (Entry<IThreshold, Iterator<Float>> thresholdEntry : thresholdMap.entrySet()) {
				if(thresholdEntry.getValue().hasNext()){
					multipleSegmentator.processState(i, thresholdEntry.getKey(), thresholdEntry.getValue().next());
					hasMore = true;
				}
					
			}
			i+=delta;			
		}
		int num = 1;
		for (Marker marker : multipleSegmentator.getMarkSet().getMarkers()) {
			marker.setLabel("" +(num++));
		}
		
		
		log.debug("[extractSegments] {0}", multipleSegmentator.getMarkSet().getMarkers());
		return multipleSegmentator.getMarkSet();
	}

	public MarkerSet extractSegments(Set<IThreshold> thresholds) {
		return extractSegments(thresholds, null);
	}
}
