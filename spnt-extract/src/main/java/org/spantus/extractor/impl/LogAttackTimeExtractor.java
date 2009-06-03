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
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.logger.Logger;
/**
 * Params: threshold
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.05.30
 *
 */
public class LogAttackTimeExtractor extends AbstractExtractor {
	Logger log = Logger.getLogger(getClass());
	float threshold = .1f;
	

	
	public float getThreshold() {
		return .7f;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public LogAttackTimeExtractor() {
	}

	public LogAttackTimeExtractor(ExtractorParam param) {
	}
	
	public int getDimension() {
		return 1;
	}
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues fv = new FrameValues();
		float lat = 0;
		float max = -Float.MAX_VALUE;
		int maxIndex =0, i= 0;
		for (Float fw : window) {
			if(fw > max){
				max = fw;
				maxIndex = i;	
			}
			i++;
		}
		Float maxthresholded = max*getThreshold();
		for (int j = maxIndex-1; j > 0; --j) {
			float fm = window.get(j); 
			if( fm < maxthresholded){
				lat = (float)Math.log10(maxIndex-j);
				lat *=10;
				break;
			}
		}
		fv.add(lat);
		return fv;
	}	
	public String getName() {
		return ExtractorEnum.LOG_ATTACK_TIME.toString();
	}
}
