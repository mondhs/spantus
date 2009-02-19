/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
package org.spantus.exp.segment.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
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
	
	public ComparisionResult compare(MarkerSet original,MarkerSet test){
		ComparisionResult result = new ComparisionResult();
		result.setOriginal(createSequence(original)); 
		result.setTest(createSequence(test));
		compare(result);
		result.getParams().clear();
		result.getParams().putAll(analyze(test));
		return result;
	}
	
	protected Map<String, Long> analyze(MarkerSet markerSet){
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
	
	protected FrameValues createSequence(MarkerSet markerSet1){
		return createSequence(markerSet1, 1);
	}
	
	protected FrameValues createSequence(MarkerSet markerSet1, float coef){
		FrameValues seq = new FrameValues();
		seq.setSampleRate(1000/STEP_IN_MILS.floatValue());
		long lastEnd = 0;
		for (Marker m1 : markerSet1.getMarkers()) {
			m1.getStart();
			long start = m1.getStart()/STEP_IN_MILS;
			
			for (int i = 0; i < start-lastEnd; i++) {
				seq.add(0f);
			}
			long length = m1.getLength()/STEP_IN_MILS;
			for (int i = 0; i < length; i++) {
				seq.add(coef);
			}
			lastEnd = start+length;
		} 
		seq.add(0f);
		return seq;
	}
	
	protected FrameValues compare(ComparisionResult result){
		FrameValues seq = new FrameValues();
		int maxSize = Math.max(result.getOriginal().size(), 
				result.getTest().size());
		Iterator<Float> i1 = result.getOriginal().iterator();
		Iterator<Float> i2 = result.getTest().iterator();
		Assert.isTrue(result.getOriginal().getSampleRate() == result.getTest().getSampleRate());
		seq.setSampleRate(result.getOriginal().getSampleRate());
		int totalCount = 0;
		int errorCount = 0;
		for (int i = 0; i < maxSize; i++) {
			totalCount++;
			Float v1 = i1.hasNext()?i1.next():0;
			Float v2 = i2.hasNext()?i2.next():0;
			Float r = 0f;
			if(v1.equals(v2)){
				r = 0f;
			}else if(v1 > v2){
				errorCount++;
				r = 1f;
			}else{
				errorCount++;
				r = -1f;
			}
			seq.add(r);
		}
		result.setSequenceResult(seq);
		result.setTotalResult(((float)errorCount)/totalCount);
		log.debug("Simple comparition result: " + result.getTotalResult());
		return seq;
	}
}
