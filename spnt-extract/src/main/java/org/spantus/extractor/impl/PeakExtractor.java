/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2007 Mindaugas Greibus (spantus@gmail.com)
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
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractor3D;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.13
 *
 */
public class PeakExtractor extends AbstractExtractor {

	
	AbstractExtractor3D extractor3D = 
		new FFTExtractor();
//		new MFCCExtractor();
//		new LPCExtractor();

	protected FrameVectorValues calculateExtr3D(FrameValues window){
		syncLPCParams();
		return extractor3D.calculateWindow(window);
	}
	
	private void syncLPCParams(){
		extractor3D.setConfig(getConfig());
	}
	
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues extrValues = calculateExtr3D(window);
		FrameValues calculatedValues = new FrameValues();
		float peak = -Float.MAX_VALUE;
		for (List<Float> vector : extrValues) {
			Integer maxIndex = 0;
			int i = 0;
			for (Float float2 : vector) {
				if(peak<float2){
					peak = float2;
					maxIndex = i;
					
				}
				i++;
			}
			calculatedValues.add(
					maxIndex.floatValue());
			peak = -Float.MAX_VALUE;
			maxIndex = 0;
		}
		
		
		return calculatedValues;
	}

	
	public String getName() {
		return ExtractorEnum.PEAK_EXTRACTOR.toString();
	}

}
