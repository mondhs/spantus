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
package org.spantus.exp.segment.beans;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 *
 */
public class ComparisionResult {
	
	public enum paramEnum{avgTstLength, minTstLength, maxTstLength, 
		avgTstDistance, minTstDistance, maxTstDistance};
	
	private String name;
	
	private FrameValues sequenceResult;

	private FrameValues signal;

	private IClassifier threshold;
	
	private FrameValues original;
	
	private FrameValues test;
	
	private float totalResult;
	
	private MarkerSetHolder originalMarkers;
	
	private MarkerSetHolder testMarkers;
	
	private Map<String, Number> params;
	
	private String noiseType;
	
	private String noiseLevel;
	
	private String classifier;
	
	
	public Map<String, Number> getParams() {
		if(params == null){
			params = new HashMap<String, Number>();
		}
		return params;
	}

	public FrameValues getOriginal() {
		return original;
	}

	public void setOriginal(FrameValues original) {
		this.original = original;
	}

	public FrameValues getTest() {
		return test;
	}

	public void setTest(FrameValues test) {
		this.test = test;
	}

	public FrameValues getSequenceResult() {
		return sequenceResult;
	}

	public void setSequenceResult(FrameValues sequenceResult) {
		this.sequenceResult = sequenceResult;
	}

	public float getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(float totalResult) {
		this.totalResult = totalResult;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FrameValues getSignal() {
		return signal;
	}

	public void setSignal(FrameValues signal) {
		this.signal = signal;
	}

	public IClassifier getThreshold() {
		return threshold;
	}

	public void setThreshold(IClassifier threshold) {
		this.threshold = threshold;
	}

	public MarkerSetHolder getOriginalMarkers() {
		return originalMarkers;
	}

	public void setOriginalMarkers(MarkerSetHolder originalMarkers) {
		this.originalMarkers = originalMarkers;
	}

	public MarkerSetHolder getTestMarkers() {
		return testMarkers;
	}

	public void setTestMarkers(MarkerSetHolder testMarkers) {
		this.testMarkers = testMarkers;
	}

	public String getNoiseType() {
		return noiseType;
	}

	public void setNoiseType(String noiseType) {
		this.noiseType = noiseType;
	}

	public String getNoiseLevel() {
		return noiseLevel;
	}

	public void setNoiseLevel(String noiseLevel) {
		this.noiseLevel = noiseLevel;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

}
