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
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
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

public class SimpleSegmentatorServiceImpl implements ISegmentatorService {
	
	Logger log = Logger.getLogger(getClass());
	public MarkerSet extractSegments(Set<IThreshold> thresholds) {
		return extractSegments(thresholds, null);
	}

	protected Float getWeight(SegmentatorParam param, IThreshold threshold){
		if(param == null || param.getJoinWeights() == null
				|| !param.getJoinWeights().containsKey(threshold) ){
			return 1f;
		}
		
		return param.getJoinWeights().get(threshold);
	}
	
	public MarkerSet extractSegments(Set<IThreshold> thresholds, SegmentatorParam param ) {
		log.debug("[extractSegments] thresholds.size:" + thresholds.size());
		MarkerSet markerSet = new MarkerSet();
		Float sampleRate = null;
		
		LinkedHashMap<Integer, Float> statesSums = new LinkedHashMap<Integer, Float>();
		
		for (IThreshold threshold : thresholds){
			int i = 0;
			if(sampleRate == null){
				sampleRate = threshold.getState().getSampleRate();
			}else{
				//should be same for all threshold states
				Assert.isTrue(sampleRate.equals(threshold.getState().getSampleRate()),
						threshold.getName()+
						" should be same for all threshold states" + 
						sampleRate + " == " + threshold.getState().getSampleRate());
			}
			
			for (Float float1 : threshold.getState()) {
				safeSum(statesSums, i++, float1,  getWeight(param, threshold));
			}
		}
		
		
		SegmentationCtx ctx = new SegmentationCtx();
		ctx.setMarkerSet(markerSet);
		ctx.setSampleRate(sampleRate);

		int count = thresholds.size();
		int index = 0;
		for (Entry<Integer, Float> stateSum : statesSums.entrySet()) {
			ctx.setCurrentState(stateSum.getValue()/count > .5?1f:0f);
			ctx.setCurrentMoment((float)index);
			processState(ctx);
			index++;
		}
		ctx.setCurrentState(0f);
		processState(ctx);
		log.debug("[extractSegments] found segments:" + markerSet.getMarkers().size());
		
		return ctx.getMarkerSet();
	}
	/**
	 * 
	 * @param statesSums
	 * @param i
	 * @param f
	 */
	public void safeSum(Map<Integer, Float> statesSums ,Integer i, Float f, Float weight){
		if(!statesSums.containsKey(i)){
			statesSums.put(i, 0f);
		}
		statesSums.put(i, statesSums.get(i)+(f*weight));
	}
	
	protected void processState(SegmentationCtx ctx){
		if(ctx.getPreviousState() == null){
			ctx.setPreviousState(ctx.getCurrentState());
		}
		int diff = ctx.getCurrentState().compareTo(ctx.getPreviousState());
		if(diff > 0){
			segmentStarted(ctx);
		}else if(diff < 0){
			if(ctx.getCurrentMarker() != null){
				segmentFinished(ctx);
			}
		}
		ctx.setPreviousState(ctx.getCurrentState());
	}
	protected void segmentStarted(SegmentationCtx ctx){
		ctx.setCurrentMarker(new Marker());
		Long started = getTime(ctx.getCurrentMoment(),ctx.getSampleRate());
		ctx.getCurrentMarker().setStart(started);
	}
	
	protected void segmentFinished(SegmentationCtx ctx){
		Long end = getTime(ctx.getCurrentMoment(), ctx.getSampleRate());
		Long length = end-ctx.getCurrentMarker().getStart();
		ctx.getCurrentMarker().setLength(length);
		ctx.getCurrentMarker().setLabel("" + ctx.getMarkerSet().getMarkers().size());
		log.debug(MessageFormat.format("[segmentFinished] segment: {0} {1}:{2} ", 
				ctx.getCurrentMarker().getLabel(),
				ctx.getCurrentMarker().getStart(),
				ctx.getCurrentMarker().getLength())
				);
		ctx.getMarkerSet().getMarkers().add(ctx.getCurrentMarker());
		ctx.setCurrentMarker(null);
	}

	
	protected Long getTime(Float f, Float sampleRate){
		return BigDecimal.valueOf((f*1000)/sampleRate).setScale(0, RoundingMode.HALF_UP).longValue();
	}

	class SegmentationCtx{
		Float previousState;
		
		Float currentState;
		
		Float currentMoment;
		
		Float sampleRate;
		
		MarkerSet markerSet;
		
		Marker currentMarker;

		public MarkerSet getMarkerSet() {
			return markerSet;
		}

		public void setMarkerSet(MarkerSet markerSet) {
			this.markerSet = markerSet;
		}

		public Marker getCurrentMarker() {
			return currentMarker;
		}

		public void setCurrentMarker(Marker currentMarker) {
			this.currentMarker = currentMarker;
		}

		public Float getPreviousState() {
			return previousState;
		}

		public void setPreviousState(Float previousState) {
			this.previousState = previousState;
		}

		public Float getCurrentState() {
			return currentState;
		}

		public void setCurrentState(Float currentState) {
			this.currentState = currentState;
		}

		public Float getCurrentMoment() {
			return currentMoment;
		}

		public void setCurrentMoment(Float currentMomonet) {
			this.currentMoment = currentMomonet;
		}

		public Float getSampleRate() {
			return sampleRate;
		}

		public void setSampleRate(Float sampleRate) {
			this.sampleRate = sampleRate;
		}

	
	}


}
