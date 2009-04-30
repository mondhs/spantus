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
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.17
 *
 */
public class SpectralEntropy extends AbstractSpectralExtractor {

	
	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameValues rtnValues = super.calculateWindow(window);
		for (List<Float> fv : val3d) {
			float bottom = 0;
			for (Float current : fv) {
				//|X[i]|^2
				bottom += Math.pow(Math.abs(current),2);
			}
			float entropy = 0;
			for (Float current : fv) {
				double part = (Math.pow(Math.abs(current),2)/bottom);
				if(part == 0) continue;
				entropy += (part) * Math.log10(part) ;
			}
			rtnValues.add(-entropy);
		}
		return rtnValues;
	}


	
	public String getName() {
		return ExtractorEnum.SPECTRAL_ENTROPY_EXTRACTOR.toString();
	}



}
