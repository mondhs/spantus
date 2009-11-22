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
package org.spantus.extractor.modifiers;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.impl.EnergyExtractor;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.logger.Logger;
/**
 * 
 * Params logaritmic
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.06.02
 *
 */
public class LogExtractor extends AbstractExtractor {
	Logger log = Logger.getLogger(getClass());
	

	private IExtractor extractor;

	
	
	public LogExtractor() {
		getParam().setClassName(LogExtractor.class.getSimpleName());
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = new FrameValues();
		FrameValues fv = getExtractor().calculateWindow(window);
		for (Float float1 : fv) {
			calculatedValues.add((float)Math.log(float1));
		}
		return calculatedValues;
	}	
	
	
	
	
	public String getName() {
		return ExtractorModifiersEnum.mean.name()+"_" + getExtractor().getName();
	}
	
	@Override
	public void setConfig(IExtractorConfig conf) {
		extractor.setConfig(conf);
	}
	@Override
	public IExtractorConfig getConfig() {
		return extractor.getConfig();
	}
	
	public IExtractor getExtractor() {
		if(extractor == null){
			extractor = new EnergyExtractor();
		}
		return extractor;
	}

	public void setExtractor(IExtractor extractor) {
		this.extractor = extractor;
	}

}
