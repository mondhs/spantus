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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.math.MatrixUtils;
import org.spantus.math.windowing.Windowing;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.math.windowing.WindowingFactory;
import org.spantus.segment.AbstractSegmentatorService;
import org.spantus.segment.SegmentatorParam;


/**
 * Simple implementation of segmentation off-line algorithm. 
 * it merges few classifiers result. 
 * 
 * @author mondhs
 * 
 */
public class BasicSegmentatorServiceImpl extends AbstractSegmentatorService {

	private Logger log = Logger.getLogger(getClass());
	private Windowing windowing;
	
	
	/**
	 * 
	 */
	public MarkerSetHolder extractSegments(Collection<IClassifier> classifiers,
			SegmentatorParam param) {
		log.debug("[extractSegments] thresholds.size:" + classifiers.size());
		MarkerSetHolder holder = new MarkerSetHolder();
		//if classifiers only one nothing to merge
		if(classifiers.size() == 1){
			IClassifier classfier = classifiers.iterator().next();
			MarkerSet phones = classfier.getMarkSet();
			holder.getMarkerSets().put(phones.getMarkerSetType(), phones);//phone
			return holder;
			
		}
			
		MarkerSet markerSet = new MarkerSet();
		LinkedHashMap<Long, Map<String, Double>> statesSums = caclculateStatesSums(classifiers, param);

		SegmentationCtx ctx = new SegmentationCtx();
		ctx.setMarkerSet(markerSet);

		for (Entry<Long, Map<String, Double>> stateSum : statesSums.entrySet()) {
			Double vote = calculateVoteResult(stateSum.getValue().values());
			ctx.setCurrentState(vote > .3 ? 1f : 0f);
			ctx.setCurrentMoment(stateSum.getKey());
			processState(ctx);
		}
		ctx.setCurrentState(0f);
		processState(ctx);
		log.debug("[extractSegments] found segments:"
				+ markerSet.getMarkers().size());

		MarkerSet phones = ctx.getMarkerSet();
		phones.setMarkerSetType(MarkerSetHolderEnum.phone.name());
		holder.getMarkerSets().put(phones.getMarkerSetType(), phones);//phone
		
		
		
		return holder;
	}
	
	/**
	 * Calculate state for each frame using all classifiers data
	 * @param classifiers
	 * @param param
	 * @return
	 */
	protected LinkedHashMap<Long, Map<String, Double>> caclculateStatesSums(Collection<IClassifier> classifiers, SegmentatorParam param){
		LinkedHashMap<Long,Map<String, Double>> statesSums = new LinkedHashMap<Long, Map<String, Double>>();

		Map<String, Iterator<Marker>> markerIterators = new HashMap<String, Iterator<Marker>>();
		
		for (IClassifier classifier : classifiers) {
			markerIterators.put(classifier.getName(), classifier.getMarkSet().getMarkers().iterator());
		}
		boolean hasNext = true;
		Map<String, Marker> lastMarkers = new HashMap<String, Marker>();
		while(hasNext){
			hasNext = false;
			for (Entry<String, Iterator<Marker>> markerIterator : markerIterators.entrySet()) {
				if(!markerIterator.getValue().hasNext()){
					continue;
				}
				hasNext = true;
				Marker marker = markerIterator.getValue().next();
				Marker lastMarker = lastMarkers.get(markerIterator.getKey());
				lastMarkers.put(markerIterator.getKey(), marker);
				
				long startIndex = lastMarker == null?0:lastMarker.getEnd();
				
				for (long index= startIndex; index < marker.getStart(); index++) {
					safeSum(statesSums, index, 0D, param, markerIterator.getKey());
				}
				List<Double> window = MatrixUtils.fill(marker.getLength().intValue(), 1D);
				getWindowing().apply(window);
				long index = marker.getStart();
				for (Double value : window) {
					safeSum(statesSums, index++, value, param, markerIterator.getKey());
				}
				
//				log.error("[caclculateStatesSums]" + markerIterator.getKey() + marker);
			}
		}
		
		return statesSums;
	}
	
	/**
	 * 
	 * @param param
	 * @param threshold
	 * @return
	 */
	protected Double getWeight(SegmentatorParam param, String threshold) {
		if (param == null || param.getJoinWeights() == null
				|| !param.getJoinWeights().containsKey(threshold)) {
			return 1D;
		}

		return param.getJoinWeights().get(threshold);
	}

	/**
	 * 
	 * @param statesSums
	 * @param time
	 * @param value
	 */
	protected void safeSum(Map<Long, Map<String,Double>> statesSums, Long time, Double value,
			SegmentatorParam param, String classifier) {
		Double weight =  getWeight(param, classifier);
		Map<String, Double> existValue = statesSums.get(time);
		if (existValue == null) {
			existValue = new HashMap<String, Double>();
			statesSums.put(time, existValue);
		}
		existValue.put(classifier, value);
//		statesSums.put(time, existValue + (value * weight));
	}

	/**
	 * 
	 * @param ctx
	 */
	protected void processState(SegmentationCtx ctx) {
		if (ctx.getPreviousState() == null) {
			ctx.setPreviousState(ctx.getCurrentState());
		}
		int diff = ctx.getCurrentState().compareTo(ctx.getPreviousState());
		if (diff > 0) {
			segmentStarted(ctx);
		} else if (diff < 0) {
			if (ctx.getCurrentMarker() != null) {
				segmentFinished(ctx);
			}
		}
		ctx.setPreviousState(ctx.getCurrentState());
		ctx.setPrevMoment(ctx.getCurrentMoment());
	}

	/**
	 * 
	 * @param ctx
	 */
	protected void segmentStarted(SegmentationCtx ctx) {
		ctx.setCurrentMarker(new Marker());
		Long started = ctx.getCurrentMoment();
		ctx.getCurrentMarker().setStart(started-10);
		log.debug("marker started: " + started);
	}

	/**
	 * 
	 * @param ctx
	 */
	protected void segmentFinished(SegmentationCtx ctx) {
		Long end = ctx.getPrevMoment();
		if(end - ctx.getCurrentMarker().getStart()==0){
			log.error("[segmentFinished] too short for segment" + ctx.getCurrentMarker());
			ctx.setCurrentMarker(null);
			return;
		}
		ctx.getCurrentMarker().setEnd(end+10);
		ctx.getCurrentMarker().setLabel(
				"" + ctx.getMarkerSet().getMarkers().size());
		if (log.isDebugMode()) {
			log.debug(MessageFormat.format(
					"[segmentFinished] segment: {0} {1}:{2} ", ctx
							.getCurrentMarker().getLabel(), ctx
							.getCurrentMarker().getStart(), ctx
							.getCurrentMarker().getEnd()));
		}
		ctx.getMarkerSet().getMarkers().add(ctx.getCurrentMarker());
		ctx.setCurrentMarker(null);
	}

	public Windowing getWindowing() {
		if(windowing == null){
			windowing = WindowingFactory.createWindowing(WindowingEnum.Welch);
		}
		return windowing;
	}

	public void setWindowing(Windowing windowing) {
		this.windowing = windowing;
	}

	/**
	 * 
	 * @param f
	 * @param sampleRate
	 * @return
	 */
	// protected Long getTime(Float f, Float sampleRate){
	// return BigDecimal.valueOf(f*sampleRate).setScale(0,
	// RoundingMode.HALF_UP).longValue();
	// }

}
