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

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.logger.Logger;
import org.spantus.math.services.MFCCService;
import org.spantus.math.services.MathServicesFactory;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class MFCCExtractor extends AbstractExtractor3D {
	static Logger log = Logger.getLogger(MFCCExtractor.class);
	
	MFCCService service;

	public int getDimension() {
		return 16;
	}

	public String getName() {
		return ExtractorEnum.MFCC_EXTRACTOR.toString();
	}
	

	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = new FrameVectorValues();
		
		List<Float> floats = getService().calculateMFCC(window,
							getConfig().getSampleRate());
		calculatedValues.add(floats);
				
		return calculatedValues;
	}
	public MFCCService getService() {
		if(service == null){
			service = MathServicesFactory.createMFCCService();
		}
		return service;
	}
	
}
