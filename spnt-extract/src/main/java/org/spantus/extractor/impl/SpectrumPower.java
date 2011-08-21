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
 * Created 2009.06.07
 *
 */
public class SpectrumPower extends AbstractSpectralExtractor {

	
	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameValues rtnValues = super.calculateWindow(window);
		int bin = 10;
		for (List<Double> fv : val3d) {
			rtnValues.clear();
			int i = 0;
			for (Double current : fv) {
				i++;
				if(i != bin) {
					continue;
				}
				rtnValues.add(current);
			}
			
		}

		return rtnValues;
	}


	
	public String getName() {
		return ExtractorEnum.SPECTRUM_POWER_EXTRACTOR.name();
	}



}
