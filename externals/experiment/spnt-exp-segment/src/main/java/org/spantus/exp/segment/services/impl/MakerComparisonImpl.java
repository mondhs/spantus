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
package org.spantus.exp.segment.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ComparisionResult.paramEnum;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class MakerComparisonImpl implements MakerComparison{
	
	protected Logger log = Logger.getLogger(getClass());
	
	public static final Long STEP_IN_MILS = 10L; 
	
	
	/**
	 * 
	 */
	public ComparisionResult compare(MarkerSetHolder originalMarkers,MarkerSetHolder testMarkers){
		ComparisionResult result = new ComparisionResult();
		result.setOriginal(createSequence(originalMarkers)); 
		result.setTest(createSequence(testMarkers));
		compare(result);
		result.getParams().clear();
		result.getParams().putAll(analyze(testMarkers));
		result.setOriginalMarkers(originalMarkers);
		result.setTestMarkers(testMarkers);
		return result;
	}
	
	
	protected Map<String, Long> analyze(MarkerSetHolder markerSetHolder){
		MarkerSet markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		if(markerSet == null){
			markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		}
		Map<String, Long> map = new HashMap<String, Long>();
		Long minLength = null;
		Long maxLength = null;
		Long avgLength = null;

		Long minDistance = null;
		Long maxDistance = null;
		Long avgDistance = null;

		Marker previous = null;
		for (Marker m : markerSet.getMarkers()) {
			if(previous == null){
				previous = m;
				avgLength = m.getLength();
				minLength = m.getLength();
				maxLength = m.getLength();
			}else{
				Long distance = m.getStart()-(previous.getStart()+previous.getLength());
				if(minDistance == null){
					minDistance = distance;
					maxDistance = distance;
					avgDistance = distance;
				}
				minDistance = Math.min(minDistance, distance);
				maxDistance = Math.max(maxDistance, distance);
				avgDistance = (avgDistance+distance)/2;
			}
			minLength = Math.min(minLength,m.getLength());
			maxLength =Math.max(maxLength, m.getLength()); 
			avgLength = (avgLength+m.getLength())/2;
		}
		map.put(paramEnum.minTstLength.name(), minLength);
		map.put(paramEnum.maxTstLength.name(), maxLength);
		map.put(paramEnum.avgTstLength.name(), avgLength);
		map.put(paramEnum.minTstDistance.name(), minDistance);
		map.put(paramEnum.maxTstDistance.name(), maxDistance);
		map.put(paramEnum.avgTstDistance.name(), avgDistance);

		
		return map;
	}
	/**
	 * 
	 * @param markerSetHolder
	 * @return
	 */
	protected FrameValues createSequence(MarkerSetHolder markerSetHolder){
		MarkerSet ms = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		if(ms == null){
			ms = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		}
		return createSequence(ms, 1D);
	}
	
	/**
	 * 
	 * @param markerSet1
	 * @return
	 */
	protected FrameValues createSequence(MarkerSet markerSet1){
		return createSequence(markerSet1, 1D);
	}
	
	protected FrameValues createSequence(MarkerSet markerSet1, Double coef){
		FrameValues seq = new FrameValues();
		seq.setSampleRate(1000/STEP_IN_MILS.doubleValue());
		long lastEnd = 0;
		for (Marker m1 : markerSet1.getMarkers()) {
			m1.getStart();
			long start = m1.getStart()/STEP_IN_MILS;
			
			for (int i = 0; i < start-lastEnd; i++) {
				seq.add(0D);
			}
			long length = m1.getLength()/STEP_IN_MILS;
			for (int i = 0; i < length; i++) {
				seq.add(coef);
			}
			lastEnd = start+length;
		} 
		seq.add(0D);
		return seq;
	}
	
	protected FrameValues compare(ComparisionResult result){
		FrameValues seq = new FrameValues();
		int maxSize = Math.max(result.getOriginal().size(), 
				result.getTest().size());
		Iterator<Double> i1 = result.getOriginal().iterator();
		Iterator<Double> i2 = result.getTest().iterator();
		Assert.isTrue(result.getOriginal().getSampleRate() == result.getTest().getSampleRate());
		seq.setSampleRate(result.getOriginal().getSampleRate());
		int totalCount = 0;
		int errorCount = 0;
		for (int i = 0; i < maxSize; i++) {
			totalCount++;
			Double v1 = i1.hasNext()?i1.next():0;
			Double v2 = i2.hasNext()?i2.next():0;
			Double r = 0D;
			if(v1.equals(v2)){
				r = 0D;
			}else if(v1 > v2){
				errorCount++;
				r = 1D;
			}else{
				errorCount++;
				r = -1D;
			}
			seq.add(r);
		}
		result.setSequenceResult(seq);
		result.setTotalResult(((double)errorCount)/totalCount);
		log.debug("Simple comparition result: " + result.getTotalResult());
		return seq;
	}
}
