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
package org.spantus.core.extractor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.01
 *
 */
public class DefaultExtractorInputReader implements IExtractorInputReader{
	protected Logger log = Logger.getLogger(DefaultExtractorInputReader.class);

	FrameValues values = new FrameValues();

	private int index;
	private Long offset = 0L;
	
	IExtractorConfig config;

	Set<IExtractor> extractorRegister = new LinkedHashSet<IExtractor>();
	Set<IExtractorVector> extractorRegister3D = new LinkedHashSet<IExtractorVector>();
	Set<IGeneralExtractor> generalExtractorRegister = new LinkedHashSet<IGeneralExtractor>();

	public DefaultExtractorInputReader() {
		initValues();
	}

	public void put(Long sample, float value) {
		values.add(index++, value);
		if (index >= config.getFrameSize()) {
			pushValues(sample);
			initValues();
		}
	}

	private void initValues() {
		offset += index;
		values = new FrameValues();
		index = 0;
	}

	public void registerExtractor(IExtractor extractor) {
		extractor.setConfig(getConfig());
		extractorRegister.add(extractor);
		generalExtractorRegister.add(extractor);
	}
	public void registerExtractor(IExtractorVector extractor) {
		extractor.setConfig(getConfig());
		extractorRegister3D.add(extractor);
		generalExtractorRegister.add(extractor);
	}
	
	
	public void registerExtractor(IGeneralExtractor extractor) {
		if(extractor instanceof IExtractor){
			registerExtractor((IExtractor)extractor);
		}else if(extractor instanceof IExtractorVector){
			registerExtractor((IExtractorVector)extractor);
		}
		
	}

	


	public void pushValues(Long sample) {
		for (IGeneralExtractor element : generalExtractorRegister) {
			element.putValues(sample, values);
			
		}
	}

	public Set<IExtractor> getExtractorRegister() {
		return extractorRegister;
	}
	public Set<IExtractorVector> getExtractorRegister3D() {
		return extractorRegister3D;
	}

	public IExtractorConfig getConfig() {
		if(config == null){
			config = new DefaultExtractorConfig();
		}
		return config;
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
		for (IGeneralExtractor iExtr : generalExtractorRegister) {
			iExtr.setConfig(config);
		}
	}

	public Long getOffset() {
		return offset;
	}

}
