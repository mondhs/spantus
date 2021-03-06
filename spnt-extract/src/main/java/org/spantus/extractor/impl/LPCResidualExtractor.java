/**
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

import java.util.Iterator;
import java.util.LinkedList;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.math.MatrixUtils;
/**
 *  Linear predictive coding residual energy
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class LPCResidualExtractor extends AbstractExtractor {
	private AbstractExtractorVector extractorVector =
		new LPCExtractor();
	private LinkedList<Double> buffer;

	protected FrameVectorValues calculateExtr3D(FrameValues window){
		syncLPCParams();
		return extractorVector.calculateWindow(window);
	}
	
	private void syncLPCParams(){
		extractorVector.setConfig(getConfig());
	}
	
	
	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues extrValues = calculateExtr3D(window);
		FrameValues calculatedValues = newFrameValues(window);
		int order = extrValues.get(0).size();
		LinkedList<Double> bufferValues = getBuffer(order); 
		Double valueSum = 0D;
		Double predictedSum = 0D;
		for (Double value : window) {
			bufferValues.poll();
			bufferValues.add(value);
			Double predicted = 0D;
			Iterator<Double> coefIter = extrValues.get(0).iterator();
			for (Double bufferedVal : getBuffer(order)) {
				predicted += bufferedVal * coefIter.next();
			}
			valueSum += value;
			predictedSum += predicted;
		}
		calculatedValues.add(Math.abs(valueSum-predictedSum));
		return calculatedValues;
	}
	
	LinkedList<Double> getBuffer(int order){
		if(buffer == null){
			buffer = new LinkedList<Double>();
			buffer.addAll(MatrixUtils.zeros(order));
		}
		return buffer;
	}
	
	public String getName() {
		return ExtractorEnum.LPC_RESIDUAL_EXTRACTOR.name();
	}


}
