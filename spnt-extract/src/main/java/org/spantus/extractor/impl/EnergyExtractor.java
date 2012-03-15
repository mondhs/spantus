/*
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
package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.logger.Logger;
import org.spantus.utils.ExtractorParamUtils;
/**
 * 
 * Params logaritmic
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class EnergyExtractor extends AbstractExtractor {
	Logger log = Logger.getLogger(getClass());
	final String PARAM_WINDOW_SIZE = this.getClass().getSimpleName() + ".windowSize";  
	final String PARAM_LOGARITMIC = "EnergyExtractor.logaritmic";
	
	static final boolean DEFAULT_LOGARITMIC = false;
	
	public EnergyExtractor() {
		getParam().setClassName(EnergyExtractor.class.getSimpleName());
		initParam();
	}

	public EnergyExtractor(ExtractorParam param) {
		setParam(param);
		initParam();
	}
	public void initParam(){

	}
	
	public int getDimension() {
		return 1;
	}
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = newFrameValues(window);
		double windowVal = 0;
		for (Double float1 : window) {
			float1 = Math.pow(float1, 2);
			Boolean isLog = getLogaritmic(); 
//			if(Boolean.TRUE.equals(isLog) && float1 != 0){
//				double fE =  (10*Math.log10(float1));
//				 float1 = Double.isNaN(fE) ? 0 : fE;
//			}
			windowVal += float1;
		}
		calculatedValues.add(windowVal/window.size());
		return calculatedValues;
	}	
	public String getName() {
		return ExtractorEnum.ENERGY_EXTRACTOR.name();
	}

	public Boolean getLogaritmic() {
		return ExtractorParamUtils.getValue(getParam(), PARAM_LOGARITMIC, DEFAULT_LOGARITMIC);
	}

	public void setLogaritmic(Boolean logaritmic) {
		ExtractorParamUtils.setValue(getParam(), PARAM_LOGARITMIC, logaritmic);
	}
	
}
