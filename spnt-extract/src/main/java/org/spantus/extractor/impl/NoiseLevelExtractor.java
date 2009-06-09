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
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.06.10
 *
 */
public class NoiseLevelExtractor extends AbstractSpectralExtractor {

	
	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameValues rtnValues = super.calculateWindow(window);
		for (List<Float> fv : val3d) {
			float entropy = 0;
			for (Float current : fv) {
				if(current == 0) continue;
				entropy += (current) * Math.log10(current) ;
				if(Float.isNaN(entropy)){
					Float.isNaN(entropy);
				}
				;
			}
			rtnValues.add(entropy);
		}
		return rtnValues;
	}


	
	public String getName() {
		return ExtractorEnum.NOISE_LEVEL_EXTRACTOR.toString();
	}
	
	public AbstractExtractor3D getAbstractExtractor3D() {
		if(abstractExtractor3D == null){
			abstractExtractor3D = new SpectralGainFactorExtractor();
		}
		return abstractExtractor3D;
	}



}
