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
package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.AbstractExtractor;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.06.02
 *
 */
public class NoiseLevelExtractor extends AbstractExtractor{
	
	private EnergyExtractor energyExtractor = new EnergyExtractor();
	private MeanExtractor meanExtractor = new MeanExtractor();;

	public NoiseLevelExtractor() {
		getParam().setClassName(NoiseLevelExtractor.class.getSimpleName());
	}
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = new FrameValues();
		FrameValues fv = energyExtractor.calculateWindow(window);
		Float value = fv.get(0);
		meanExtractor.calculateMean(value);
		Float noiseLevel = meanExtractor.getMean() - .75F * meanExtractor.getStdev();
		calculatedValues.add(noiseLevel);
		return calculatedValues;

	}

	
	@Override
	public void setConfig(IExtractorConfig conf) {
		energyExtractor.setConfig(conf);
	}
	@Override
	public IExtractorConfig getConfig() {
		return energyExtractor.getConfig();
	}
	
	public String getName() {
		return ExtractorEnum.NOISE_LEVEL_EXTRACTOR.toString();
	}

}
