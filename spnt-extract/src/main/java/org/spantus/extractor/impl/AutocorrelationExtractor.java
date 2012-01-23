/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
import org.spantus.math.Autocorrelation;
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
public class AutocorrelationExtractor extends AbstractExtractor{

	
	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = newFrameValues(window);
		Double ac= Autocorrelation.calc(window, 2).get(1);
		calculatedValues.add(1+ac);//ac is negative and more than -1
		return calculatedValues;

	}

	
	public String getName() {
		return ExtractorEnum.AUTOCORRELATION_EXTRACTOR.name();
	}

}
