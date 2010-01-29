/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.threshold;

import org.spantus.core.FrameValues;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class OfflineThreshold extends DynamicThreshold {
	
	@Override
	public void flush() {
		super.flush();
		Float binVal = Histogram.calculateAvgForFirstBin(Histogram.calculateHistogram(getOutputValues()));
		setCurrentThresholdValue(binVal);
		getThresholdValues().clear();
		setClassifierSampleNum(0);
		afterCalculated(0L, getOutputValues());
		
	}
	
	@Override
	protected void recacluclateCurrentThreashold(FrameValues result){
		//do nothing
	}

}
