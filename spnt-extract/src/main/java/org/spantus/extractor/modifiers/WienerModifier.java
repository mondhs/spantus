/*
 * Copyright (c) 2010 Mindaugas Greibus (spantus@gmail.com)
 * Part of program for analyze speech signal
 * http://spantus.sourceforge.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.spantus.extractor.modifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Wiener modifiers based on Sphinx
 * <p/>
 * User: mondhs
 * Date: 10.12.12
 * Time: 15.22
 */
public class WienerModifier {
    List<Double> prevInput = null;
    List<Double> prevSignal = null;
    List<Double> prevNoise = null;
    Double gamma = 0.98;
    Double etaMin = 1e-2;
    Double lambda = 0.99;
    boolean isPreviousNoise = false;
    protected float minSignal =0 ;           // minimum valid signal level
    protected float level=0;               // average signal level
    protected final float averageNumber = 1;
    protected float background=300;          // background signal level
    protected float adjustment =0.003F;
    protected float threshold = 10;


    public List<Double> calculateWindow(List<Double> fftOutput, List<Double> signal) {
        return wienerFilter(fftOutput, classify(signal));
    }

      public static float logRootMeanSquare(List<Double> samples) {
        assert samples.size() > 0;
        double sumOfSquares = 0.0f;
        for (double sample : samples) {
            sumOfSquares += sample * sample;
        }
        double rootMeanSquare = Math.sqrt(sumOfSquares / samples.size());
        rootMeanSquare = Math.max(rootMeanSquare, 1);
        return ((float)Math.log10(rootMeanSquare) * 20);
    }


        protected boolean classify(List<Double> input) {
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

    /**
     * edu/cmu/sphinx/frontend/endpoint/WienerFilter.java
     *
     * @param fftOutput
     * @return
     */
    private List<Double> wienerFilter(List<Double> fftOutput, boolean isNoise) {
        int length = fftOutput.size();
        if (prevInput == null) {
            prevInput = fftOutput;
            prevSignal = fftOutput;
            prevNoise = fftOutput;
        }
        List<Double> smoothedInput = smooth(fftOutput);
        List<Double> noise = estimateNoise(smoothedInput, isPreviousNoise);
        List<Double> signal = new ArrayList<Double>(fftOutput);
        for (int i = 0; i < length; i++) {
        	Double max = Math.max(smoothedInput.get(i)
                    - noise.get(i), 0);
        	Double s = gamma * prevSignal.get(i) + (1 - gamma) * max;
        	Double eta = Math.max(s / noise.get(i), etaMin);
            if(Double.isNaN(eta)){
            	eta = etaMin;
            }
            signal.set(i, (eta / (1 + eta) * fftOutput.get(i)));
            ;
        }
        prevInput = fftOutput;
        prevSignal = signal;
        prevNoise = noise;
        isPreviousNoise = isNoise;
        return signal;
    }

    private List<Double> smooth(List<Double> input) {
        int length = input.size();
        List<Double> smoothed = new ArrayList<Double>(input);
        for (int i = 1; i < length - 1; i++) {
        	Double val = (input.get(i) + input.get(i - 1) + input.get(i - 1) +
                    prevInput.get(i)) / 4;
            smoothed.set(i, val);
        }
        Double val0 = (input.get(0) + input.get(1) + prevInput.get(0)) / 3;
        smoothed.set(0, val0);
        Double valLast = (input.get(length - 1) + input.get(length - 2)
                + prevInput.get(length - 1)) / 3;
        smoothed.set(length - 1, valLast);

        return smoothed;
    }

    private List<Double> estimateNoise(List<Double> smoothedInput, boolean previousNoise) {
        List<Double> noise = new ArrayList<Double>(smoothedInput);
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
}
