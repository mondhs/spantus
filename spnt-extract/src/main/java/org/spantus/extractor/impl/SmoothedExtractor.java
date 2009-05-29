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
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;
/**
 * 
 * Params logaritmic
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.13
 *
 */
public class SmoothedExtractor extends AbstractExtractor {
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
		return "SMOOTHED_" + getExtractor().getName();
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
