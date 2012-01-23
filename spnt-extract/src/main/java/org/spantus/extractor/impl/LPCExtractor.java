/*
 * Copyright (c) 2010 Mindaugas Greibus (spantus@gmail.com)
 * Part of program for analyze speech signal
 * http://spantus.sourceforge.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.spantus.extractor.impl;

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.math.services.LPCService;
import org.spantus.math.services.MathServicesFactory;
/**
 * Linear predictive coding feature extractor
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class LPCExtractor extends AbstractExtractorVector {
//	private Logger log = Logger.getLogger(LPCExtractor.class);
//	private int step = 0;
	private LPCService lpcService;
	
	public LPCExtractor() {
		getParam().setClassName(LPCExtractor.class.getSimpleName());
	}

	public LPCExtractor(ExtractorParam param) {
		setParam(param);
	}

	public int getDimension() {
		return 12;
	}


	public String getName() {
		return ExtractorEnum.LPC_EXTRACTOR.name();
	}
	protected FrameVectorValues calculateWindow(FrameValues windowedWindow, FrameValues realValues){
		return calculateWindow(realValues);
	}

	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = new FrameVectorValues();
		calculatedValues.setSampleRate(window.getSampleRate());
		List<Double> lpc = getLpcService().calculateLPC(window, getDimension()).getResult();
		calculatedValues.add(lpc);
		return calculatedValues;

	}

	public LPCService getLpcService() {
		if(lpcService == null){
			lpcService = MathServicesFactory.createLPCService();
		}
		return lpcService;
	}

}
