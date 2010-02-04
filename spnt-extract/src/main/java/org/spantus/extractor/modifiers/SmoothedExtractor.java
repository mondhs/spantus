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
import org.spantus.math.VectorUtils;
/**
 * 
 * Smothing with moving average
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.13
 *
 */
public class SmoothedExtractor extends AbstractExtractorModifier {
	Logger log = Logger.getLogger(getClass());
	

	private IExtractor extractor;
	
	private FrameValues smooth = new FrameValues();

	private int smothingSize = 20;
	
	
	public SmoothedExtractor() {
		getParam().setClassName(SmoothedExtractor.class.getSimpleName());
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = new FrameValues();
		
		smooth.add(VectorUtils.avg(getExtractor().calculateWindow(window)));
		if(smooth.size()>smothingSize){
			smooth.removeFirst();
		}
		calculatedValues.add(VectorUtils.avg(smooth));

		return calculatedValues;
	}	
	
	public String getName() {
		return ExtractorModifiersEnum.smooth.name()+"_" + getExtractor().getName();
	}
	
	public IExtractor getExtractor() {
		if(extractor == null){
			extractor = new EnergyExtractor();
		}
		return extractor;
	}
	@Override
	public void setConfig(IExtractorConfig config) {
		extractor.setConfig(config);
	}
	@Override
	public IExtractorConfig getConfig() {
		return extractor.getConfig();
	}

	public void setExtractor(IExtractor extractor) {
		this.extractor = extractor;
	}

}
