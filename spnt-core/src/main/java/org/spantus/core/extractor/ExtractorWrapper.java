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

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.FrameValues;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class ExtractorWrapper implements IExtractor {

	IExtractor extractor;
	Set<IExtractorListener> listeners;

	public ExtractorWrapper(IExtractor extractor) {
		this.extractor = extractor;
	}
	
	public FrameValues calculateWindow(Long sample, FrameValues values) {

		for (IExtractorListener lstn1: getListeners()) {
			lstn1.beforeCalculated(sample, values);
		}
		FrameValues result = getExtractor().calculateWindow(sample, values);
		if(result == null){
			//nothing to calculate
			return result;
		}
	
		for (IExtractorListener lstn1: getListeners()) {
			lstn1.afterCalculated(sample, values, result);
		}
		return result;
	}

	@Override
	public FrameValues calculateWindow(FrameValues values) {
		throw new IllegalArgumentException("Not implemented");
	}

	public FrameValues getOutputValues() {
		return getExtractor().getOutputValues();
	}

	public IExtractorConfig getConfig() {
		return getExtractor().getConfig();
	}

	public Double getExtractorSampleRate() {
		return getExtractor().getExtractorSampleRate();
	}

	public String getName() {
		return getExtractor().getName();
	}


	public void setConfig(IExtractorConfig config) {
		getExtractor().setConfig(config);
	}
	public IExtractor getExtractor() {
		return extractor;
	}
	
	public Set<IExtractorListener> getListeners() {
		if(listeners == null){
			listeners = new LinkedHashSet<IExtractorListener>();
		}
		return listeners;
	}

	public void flush() {
		getExtractor().flush();
	}

	@Override
	public long getOffset() {
		return getExtractor().getOffset();
	}


}
