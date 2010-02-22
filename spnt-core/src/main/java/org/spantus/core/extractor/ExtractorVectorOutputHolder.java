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
package org.spantus.core.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
/**
 * Extracted from signal data holder.
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class ExtractorVectorOutputHolder implements IExtractorVector {
	private FrameVectorValues outputValues;
	private String name;
	private IExtractorConfig config;


	public FrameVectorValues calculate(Long sample, FrameValues values) {
		throw new IllegalAccessError("Should not be called");
	}

	public FrameVectorValues calculateWindow(FrameValues window) {
		throw new IllegalAccessError("Should not be called");
	}

	public FrameVectorValues getOutputValues() {
		return outputValues;
	}

	public IExtractorConfig getConfig() {
		return config;
	}

	public float getExtractorSampleRate() {
		return this.outputValues.getSampleRate();
	}

	public String getName() {
		return name ;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void putValues(Long sample, FrameValues values) {
		throw new IllegalAccessError("Should not be called");
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}
	public void setOutputValues(FrameVectorValues outputValues) {
		this.outputValues = outputValues;
	}
	
	@Override
	public String toString() {
		
		return getClass().getSimpleName() + "[" +
			getName() +
			" " + getOutputValues().size() + ":" + 
				(getOutputValues().iterator().next().size() ) +
				"]";
	}

	public void flush() {
		throw new IllegalAccessError("Should not be called");
	}

}
