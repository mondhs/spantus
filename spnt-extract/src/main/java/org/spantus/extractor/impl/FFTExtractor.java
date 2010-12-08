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

import java.util.ArrayList;
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
		int upperLimit = (int)(11000*fftOutput.size()/sampleRate);
		upperLimit = Math.min(upperLimit, fftOutput.size());
		fftOutput = fftOutput.subList(1, upperLimit);
		calculatedValues.add(wienerFilter(fftOutput,classify(window)));
		
		return  calculatedValues;
	}
        
        
        
    public static float logRootMeanSquare(List<Float> samples) {
        assert samples.size() > 0;
        double sumOfSquares = 0.0f;
        for (double sample : samples) {
            sumOfSquares += sample * sample;
        }
        double rootMeanSquare = Math.sqrt(sumOfSquares / samples.size());
        rootMeanSquare = Math.max(rootMeanSquare, 1);
        return ((float)Math.log10(rootMeanSquare) * 20);
    }

        
        protected float minSignal =0 ;           // minimum valid signal level
        protected float level=0;               // average signal level
        protected final float averageNumber = 1;
        protected float background=300;          // background signal level
        protected float adjustment =0.003F;
        protected float threshold = 10;

     

        
        protected boolean classify(List<Float> input) {
            float current = logRootMeanSquare(input);
            // System.out.println("current: " + current);
            boolean isSpeech = false;
            if (current >= minSignal) {
                level = ((level * averageNumber) + current) / (averageNumber + 1);
                if (current < background) {
                    background = current;
                } else {
                    background += (current - background) * adjustment;
                }
                if (level < background) {
                    level = background;
                }
                isSpeech = (level - background > threshold);
            }

            return isSpeech;


        }


        
        
        
        
        List<Float> prevInput = null;
        List<Float> prevSignal = null;
        List<Float> prevNoise = null;
        float gamma = 0.98F;
        float etaMin = 1e-2F;
        float lambda = 0.99F;
        boolean isPreviousNoise = false;



        /**
         * edu/cmu/sphinx/frontend/endpoint/WienerFilter.java
         * @param input
         * @return
         */
        private List<Float> wienerFilter(List<Float> input, boolean isNoise) {
            int length = input.size();
            if(prevInput == null){
                prevInput = input;
                prevSignal =input;
                prevNoise = input;
            }
            List<Float>  smoothedInput = smooth(input);
            List<Float>  noise = estimateNoise(smoothedInput, isPreviousNoise);
            List<Float> signal = new ArrayList<Float>(input);
            for (int i = 0; i < length; i++) {
                float max = Math.max(smoothedInput.get(i) 
                        - noise.get(i), 0);
                float s = gamma * prevSignal.get(i) + (1 - gamma) * max;
                float eta = Math.max(s / noise.get(i), etaMin);
                signal.set(i, (eta / (1 + eta) * input.get(i)));;
            }
            prevInput = input;
            prevSignal = signal;
            prevNoise = noise;
            isPreviousNoise = isNoise; 
            return signal;
        }
        
        private List<Float> smooth(List<Float> input) {
            int length = input.size();
            List<Float> smoothed = new ArrayList<Float>(input);
            for (int i = 1; i < length - 1; i++) {
                Float val = (input.get(i) + input.get(i-1) + input.get(i-1)+
                        prevInput.get(i))/4;
                smoothed.set(i, val);
            }
            Float val0 = (input.get(0) + input.get(1)+prevInput.get(0))/3;
            smoothed.set(0, val0);
            Float valLast = (input.get(length-1) + input.get(length - 2)
                    +prevInput.get(length - 1))/3;
            smoothed.set(length - 1,valLast);

            return smoothed;
        }
        
        private List<Float> estimateNoise(List<Float> smoothedInput, boolean previousNoise) {
            List<Float> noise = new ArrayList<Float>(smoothedInput);
            int length = smoothedInput.size();
            for (int i = 0; i < length; i++) {
                if (previousNoise) {
                    noise.set(i, prevNoise.get(i));
                } else {
                    noise.set(i, lambda * prevNoise.get(i) + (1 - lambda)
                            * smoothedInput.get(i));
                }
             }

            return noise;
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
