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

import java.util.LinkedList;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.logger.Logger;
import org.spantus.math.MatrixUtils;
import org.spantus.math.services.FFTService;
import org.spantus.math.services.MathServicesFactory;

/**
 * 
 * Fast Fourier transform feature
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class FFTExtractor extends AbstractExtractor3D {
	static Logger log = Logger.getLogger(FFTExtractor.class);

	private AbstractExtractor3D extractor3D = 
		new LPCExtractor();

	protected FrameVectorValues calculateExtr3D(FrameValues window){
		syncLPCParams();
		return extractor3D.calculateWindow(window);
	}
	
	private void syncLPCParams(){
		extractor3D.setConfig(getConfig());
	}
	
	Integer upperFrequency;

	FFTService service = MathServicesFactory.createFFTService();

	public String getName() {
		return ExtractorEnum.FFT_EXTRACTOR.toString();
	}
	
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameValues calculatedTempValues = window;
		FrameVectorValues calculatedValues = new FrameVectorValues();
		
		float sampleRate = getConfig().getSampleRate();
		
		
		List<Float> fftOutput = MathServicesFactory.createFFTService().calculateFFTMagnitude(calculatedTempValues);
//		upperLimit = Math.min(upperLimit, fftOutput.size());
		int upperLimit = (int)(7000*fftOutput.size()/sampleRate);
		upperLimit = Math.min(upperLimit, fftOutput.size());
		fftOutput = fftOutput.subList(1, upperLimit);
		calculatedValues.add(
				fftOutput
		);
		
		return  calculatedValues;
	}
	
	private LinkedList<Float> buffer;
	LinkedList<Float> getBuffer(int order){
		if(buffer == null){
			buffer = new LinkedList<Float>();
			buffer.addAll(MatrixUtils.zeros(order));
		}
		return buffer;
	}

//	public FrameVectorValues calculateWindow(FrameValues window) {
//		FrameVectorValues calculatedValues = new FrameVectorValues();
//
//		List<Float> fft = service.calculateFFTMagnitude(window);
//		if(getUpperFrequency() != null){
//			double coef = getUpperFrequency() / getConfig().getSampleRate();
//			double from = fft.size() - (coef*fft.size());
//			fft = fft.subList((int)from, fft.size());
//		}
//		calculatedValues.add(fft);
//		return calculatedValues;
//	}
	public void setUpperFrequency(Integer upperFrequency) {
		this.upperFrequency = upperFrequency;
	}


	public Integer getUpperFrequency(){
//		if(upperFrequency == null){
//			upperFrequency = 6600;
//		}
		return upperFrequency;
	}

}
