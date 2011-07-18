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
public class SpectralCentroid extends AbstractSpectralExtractor {
	
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameValues rtnValues = super.calculateWindow(window);
		for (List<Double> fv : val3d) {
			Double upper = 0D;
			Double bottom = 0D;
			int k = 1;
			Double normalizeCoef = coefFunction(fv.size()-1);
			for (Double f1 : fv) {
				upper += (coefFunction(k++)/normalizeCoef) * f1;
				bottom += f1;
			}
			rtnValues.add(1-(upper/bottom));
		}
		return rtnValues;
	}

	private Double coefFunction(int k){
		return (double) ((getConfig().getSampleRate() * k) / getWinowSize());
	}
	
	
	public String getName() {
		return ExtractorEnum.SPECTRAL_CENTROID_EXTRACTOR.name();
	}
	
}
