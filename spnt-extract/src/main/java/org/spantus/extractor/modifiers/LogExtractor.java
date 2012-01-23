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
package org.spantus.extractor.modifiers;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
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
public class LogExtractor extends AbstractExtractorModifier {
	Logger log = Logger.getLogger(getClass());
	

	private IExtractor extractor;

	
	
	public LogExtractor() {
		getParam().setClassName(LogExtractor.class.getSimpleName());
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = newFrameValues(window);
		calculatedValues.setSampleRate(window.getSampleRate());
		FrameValues fv = getExtractor().calculateWindow(window);
		for (Double float1 : fv) {
			if(fv.equals(0F)){
				calculatedValues.add(0D);
			}else {
				calculatedValues.add(Math.log(float1));
			}
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
