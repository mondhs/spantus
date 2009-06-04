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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.math.MatrixUtils;
import org.spantus.math.services.MathServicesFactory;
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
		int order = extrValues.get(0).size();
		LinkedList<Float> bufferValues = getBuffer(order);
		LinkedList<Float> predictedValues = new FrameValues();
		for (Float value : window) {
			bufferValues.poll();
			bufferValues.add(value);
			Float predicted = 0F;
			Iterator<Float> coefIter = extrValues.get(0).iterator();
			for (Float bufferedVal : getBuffer(order)) {
				predicted += bufferedVal * coefIter.next();
			}
			calculatedValues.add(predicted);
		}
		
		List<Float> calculatedFFTValues = MathServicesFactory.createFFTService().calculateFFTMagnitude(calculatedValues);
		
		float peak = -Float.MAX_VALUE;
		Float sum = 0F;
		Integer maxIndex = 0;
		int i = 0;
		for (Iterator<Float> iterator = calculatedFFTValues.iterator(); iterator.hasNext();) {
			Float float2 = (Float) iterator.next();
			sum += float2; 
			if(peak<float2){
				peak = float2;
				maxIndex = i;
				
			}
			i++;
		}
		Float mean = sum/calculatedFFTValues.size();
		if(peak>.5*mean*mean){
			maxIndex = 0;
		}
		calculatedValues = new FrameValues();
		calculatedValues.add(maxIndex.floatValue());
		return calculatedValues;
	}
	
	private LinkedList<Float> buffer;
	LinkedList<Float> getBuffer(int order){
		if(buffer == null){
			buffer = new LinkedList<Float>();
			buffer.addAll(MatrixUtils.zeros(order));
		}
		return buffer;
	}
	
//	public FrameValues calculateWindow(FrameValues window) {
//		FrameVectorValues extrValues = calculateExtr3D(window);
//		FrameValues calculatedValues = new FrameValues();
//		float peak = -Float.MAX_VALUE;
//		for (List<Float> vector : extrValues) {
//			Integer maxIndex = 0;
//			int i = 0;
////			for (ListIterator<Float> iterator = vector.listIterator(vector.size()); iterator.hasPrevious();) {
////			Float float2 = (Float) iterator.previous();
//			for (Iterator<Float> iterator = vector.iterator(); iterator.hasNext();) {
//				Float float2 = (Float) iterator.next();
//				if(peak<float2){
//					peak = float2;
//					maxIndex = i;
//					
//				}
//				i++;
//			}
//			calculatedValues.add(
//					maxIndex.floatValue());
//			peak = -Float.MAX_VALUE;
//			maxIndex = 0;
//		}
//		
//		
//		return calculatedValues;
//	}

	
	public String getName() {
		return ExtractorEnum.PEAK_EXTRACTOR.toString();
	}

}
