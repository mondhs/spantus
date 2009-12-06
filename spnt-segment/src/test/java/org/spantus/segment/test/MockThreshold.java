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
import org.spantus.core.threshold.IThreshold;

public class MockThreshold implements IThreshold {

	FrameValues states;
	
	public FrameValues getState() {
		return states;
	}
	public void setState(FrameValues  states) {
		this.states = states;
	}

	
	public FrameValues getThresholdValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public FrameValues calculate(Long sample, FrameValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	public FrameValues calculateWindow(FrameValues window) {
		// TODO Auto-generated method stub
		return null;
	}

	public FrameValues getOutputValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public IExtractorConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getExtractorSampleRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void putValues(Long sample, FrameValues values) {
		// TODO Auto-generated method stub

	}

	public void setConfig(IExtractorConfig config) {
		// TODO Auto-generated method stub

	}
	public void flush() {
		// TODO Auto-generated method stub
		
	}
	public MarkerSet getMarkerSet() {
		// TODO Auto-generated method stub
		return null;
	}

}
