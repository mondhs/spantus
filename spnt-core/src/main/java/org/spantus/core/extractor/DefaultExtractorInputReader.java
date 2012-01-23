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
	Set<IGeneralExtractor> generalExtractor = new LinkedHashSet<IGeneralExtractor>();

	public DefaultExtractorInputReader() {
		initValues();
	}

	public void put(Long sample, Double value) {
		values.add(index++, value);
		if (index >= config.getFrameSize()) {
			pushValues(sample, values);
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
		generalExtractor.add(extractor);
	}
	public void registerExtractor(IExtractorVector extractor) {
		extractor.setConfig(getConfig());
		extractorRegister3D.add(extractor);
		generalExtractor.add(extractor);
	}
	
	
	public void registerExtractor(IGeneralExtractor extractor) {
		if(extractor instanceof IExtractor){
			registerExtractor((IExtractor)extractor);
		}else if(extractor instanceof IExtractorVector){
			registerExtractor((IExtractorVector)extractor);
		}
		
	}

	
	public void pushValues(Long sample) {
		for (IGeneralExtractor element : generalExtractor) {
			element.putValues(sample, values);
			element.flush();
		}
	}
	/**
	 * 
	 * @param sample
	 * @param ivalues
	 */
	protected void pushValues(Long sample, FrameValues ivalues) {
		for (IGeneralExtractor element : generalExtractor) {
			element.putValues(sample, ivalues);
		}
	}

	public Set<IExtractor> getExtractorRegister() {
		return extractorRegister;
	}
	public Set<IExtractorVector> getExtractorRegister3D() {
		return extractorRegister3D;
	}

        public Set<IGeneralExtractor> getGeneralExtractor() {
            return generalExtractor;
        }

        public void setGeneralExtractor(Set<IGeneralExtractor> generalExtractor) {
            this.generalExtractor = generalExtractor;
        }

        
	public IExtractorConfig getConfig() {
		if(config == null){
			config = new DefaultExtractorConfig();
		}
		return config;
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
		for (IGeneralExtractor iExtr : generalExtractor) {
			iExtr.setConfig(config);
		}
	}

	public long getFullSampleIndex(){
		return getOffset() + index;
	}
	
	public Long getOffset() {
		return offset;
	}

	public FrameValues getValues() {
		return values;
	}

}
