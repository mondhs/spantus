
/**
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
package org.spantus.work.ui.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.reader.SupportableReaderEnum;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.26
 *
 */
public class FeatureReader implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	WorkReadersEnum readerPerspective; 
	
	Set<String> extractors;
	
	WorkUIExtractorConfig workConfig;

	Map<String, ExtractorParam> params;
	
	public Set<String> getExtractors() {
		if(extractors == null){
			extractors = new LinkedHashSet<String>();
			extractors.add(SupportableReaderEnum.spantus.name()+":"+ExtractorEnum.WAVFORM_EXTRACTOR.name());

		}
		return extractors;
	}


	public WorkReadersEnum getReaderPerspective() {
		return readerPerspective;
	}


	public void setReaderPerspective(WorkReadersEnum readerPerspective) {
		this.readerPerspective = readerPerspective;
	}

	public WorkUIExtractorConfig getWorkConfig() {
		return workConfig;
	}


	public void setWorkConfig(WorkUIExtractorConfig workConfig) {
		this.workConfig = workConfig;
	}


	public Map<String, ExtractorParam> getParameters() {
		if(params == null){
			params = new HashMap<String, ExtractorParam>();
		}
		return params;
	}


}
