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
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class WavformExtractor extends AbstractExtractor3D {
	Logger log = Logger.getLogger(getClass());

	public WavformExtractor() {}
	
	public WavformExtractor(ExtractorParam param) {
		setParam(param);
	}
	
	public int getDimension() {
		return 2;
	}
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = new FrameVectorValues();
		FrameValues fv = new FrameValues();
		Float max = Float.MIN_VALUE, min = Float.MAX_VALUE;
		for (Float float1 : window) {
			max = Math.max(max, float1);
			min = Math.min(min, float1);
		}
		fv.add(min);
		fv.add(max);
//		log.debug("min:" + min +";max:" + max);
		calculatedValues.add(fv);
		return calculatedValues;
	}	
	public String getName() {
		return ExtractorEnum.WAVFORM_EXTRACTOR.toString();
	}

}
