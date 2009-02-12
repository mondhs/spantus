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
import org.spantus.extractor.AbstractExtractor;
/**
 * Params: k and alfa 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created May 30, 2008
 *
 */
public class Loudness extends AbstractExtractor {
	AbstractExtractor energy;
	float k = 1;
	float alfa = 0.23f;
	public Loudness() {
		EnergyExtractor energyExtr = new EnergyExtractor();
		energyExtr.setLogaritmic(false);
		energy = energyExtr;
	}
	
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues fv = new FrameValues();
		for (Float fvi : energy.calculateWindow(window)) {
			float val = (float)Math.pow(k * fvi, alfa);
			if(!Float.isNaN(val)){
				fv.add(val);
			}else{
				fv.add(0f);
			}
			
		}
		return fv;
	}

	
	public String getName() {
		return ExtractorEnum.LOUDNESS_EXTRACTOR.toString();
	}

}
