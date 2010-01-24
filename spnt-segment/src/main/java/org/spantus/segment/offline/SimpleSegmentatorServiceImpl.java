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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.AbstractSegmentatorService;
import org.spantus.segment.SegmentatorParam;

/**
 * Simple implementation of segmentation off-line algorithm
 * 
 * @author mondhs
 * 
 */
public class SimpleSegmentatorServiceImpl extends AbstractSegmentatorService {

	Logger log = Logger.getLogger(getClass());

	/**
	 * 
	 */
	public MarkerSetHolder extractSegments(Set<IClassifier> classifiers,
			SegmentatorParam param) {
		log.debug("[extractSegments] thresholds.size:" + classifiers.size());
		MarkerSet markerSet = new MarkerSet();
		LinkedHashMap<Long, Float> statesSums = caclculateStatesSums(classifiers, param);
		
			// int i = 0;
			// if(sampleRate == null){
			// sampleRate = classifier.getExtractorSampleRate();
			// }else{
			// //should be same for all threshold states
			// Assert.isTrue(sampleRate.equals(classifier.getExtractorSampleRate()),
			// classifier.getName()+
			// " should be same for all threshold states" +
			// sampleRate + " == " + classifier.getExtractorSampleRate());
			// }
			// for (Float state : classifier.getState()) {
			// safeSum(statesSums, i++, state, getWeight(param, classifier));
			// }

		SegmentationCtx ctx = new SegmentationCtx();
		ctx.setMarkerSet(markerSet);
//		ctx.setSampleRate(sampleRate);

		int count = classifiers.size();
		int index = 0;
		for (Entry<Long, Float> stateSum : statesSums.entrySet()) {
			ctx.setCurrentState(stateSum.getValue() / count > .5 ? 1f : 0f);
			ctx.setCurrentMoment(stateSum.getKey());
			processState(ctx);
			index++;
		}
		ctx.setCurrentState(0f);
		processState(ctx);
		log.debug("[extractSegments] found segments:"
				+ markerSet.getMarkers().size());

		MarkerSetHolder holder = new MarkerSetHolder();
		holder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ctx.getMarkerSet());
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), ctx.getMarkerSet());
		
		return holder;
	}
	
	/**
	 * Calculate state for each frame using all classifiers data
	 * @param classifiers
	 * @param param
	 * @return
	 */
	protected LinkedHashMap<Long, Float> caclculateStatesSums(Set<IClassifier> classifiers, SegmentatorParam param){
		LinkedHashMap<Long, Float> statesSums = new LinkedHashMap<Long, Float>();

		for (IClassifier classifier : classifiers) {
			float resolution = classifier.getExtractorSampleRate();
			// last sample rate will be used as main
			MarkerSet classifierlMarkerSet = classifier.getMarkSet();
			if (classifierlMarkerSet == null) {
				continue;
			}
			long index = 0;
			for (Marker marker : classifierlMarkerSet.getMarkers()) {
				for (; index < marker.getStart(); index += resolution) {
					safeSum(statesSums, index, 0F, getWeight(param, classifier));
				}
				for (; index <= marker.getEnd(); index += resolution) {
					safeSum(statesSums, index, 1F, getWeight(param, classifier));
				}
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
	protected Float getWeight(SegmentatorParam param, IClassifier threshold) {
		if (param == null || param.getJoinWeights() == null
				|| !param.getJoinWeights().containsKey(threshold)) {
			return 1f;
		}

		return param.getJoinWeights().get(threshold);
	}

	/**
	 * 
	 * @param statesSums
	 * @param time
	 * @param value
	 */
	protected void safeSum(Map<Long, Float> statesSums, Long time, Float value,
			Float weight) {
		Float existValue = statesSums.get(time);
		if (existValue == null) {
			existValue = 0F;
			statesSums.put(time, existValue);
		}
		statesSums.put(time, existValue + (value * weight));
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
		ctx.getCurrentMarker().setStart(started);
		log.debug("marker started: " + started);
	}

	/**
	 * 
	 * @param ctx
	 */
	protected void segmentFinished(SegmentationCtx ctx) {
		Long end = ctx.getPrevMoment();
		ctx.getCurrentMarker().setEnd(end);
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
