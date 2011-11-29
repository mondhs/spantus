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
package org.spantus.segment.online.test;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.11.27
 *
 */

public class MockSegmentatorExtractor implements IExtractor {

	private IExtractorConfig config;
	
	private Double extractorSampleRate;
	
	private String name;
	
	public FrameValues calculate(Long sample, FrameValues values) {
		return calculateWindow(values);
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues rtn = new FrameValues();
		Double avg = 0D;
		for (Double float1 : window) {
			avg += float1;
		}
		avg /= window.size();
		rtn.add(avg);
		return rtn;
	}

	public FrameValues getOutputValues() {
		return null;
	}

	public IExtractorConfig getConfig() {
		return config;
	}

	public int getDimension() {
		return 0;
	}

	public Double getExtractorSampleRate() {
		return extractorSampleRate;
	}

	public void setExtractorSampleRate(Double extractorSampleRate) {
		this.extractorSampleRate = extractorSampleRate;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void putValues(Long sample, FrameValues values) {

	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getName();
	}

	public void flush() {
	}

	@Override
	public long getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
