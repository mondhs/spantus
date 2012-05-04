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
package org.spantus.segment.test;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IClassificationListener;
import org.spantus.core.threshold.IClassifier;

public class MockClassifier implements IClassifier {

//	FrameValues states;
//	
//	public FrameValues getState() {
//		return states;
//	}
//	public void setState(FrameValues  states) {
//		this.states = states;
//	}

	MarkerSet markSet;
	
	Double extractorSampleRate;
	
	public FrameValues getThresholdValues() {
		return null;
	}

	public FrameValues calculateWindow(Long sample, FrameValues values) {
		return null;
	}

	public FrameValues calculateWindow(FrameValues window) {
		return null;
	}

	public FrameValues getOutputValues() {
		return null;
	}

	public IExtractorConfig getConfig() {
		return null;
	}

	public Double getExtractorSampleRate() {
		return extractorSampleRate;
	}

	public void setExtractorSampleRate(Double extractorSampleRate) {
		this.extractorSampleRate = extractorSampleRate;
	}

	public String getName() {
		return null;
	}

	public void putValues(Long sample, FrameValues values) {
	}

	public void setConfig(IExtractorConfig config) {

	}
	public void flush() {
		
	}
	public MarkerSet getMarkSet() {
		return markSet;
	}
	public void setMarkSet(MarkerSet markerSet) {
		this.markSet = markerSet;
	}

	public boolean addClassificationListener(
			IClassificationListener classificationListener) {
		return false;
	}

	public boolean removeClassificationListener(
			IClassificationListener classificationListener) {
		return false;
	}

	@Override
	public long getOffset() {
		return 0;
	}


}
