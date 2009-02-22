/*
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

package org.spantus.core.wav;

import java.text.NumberFormat;
import java.text.ParseException;

import org.spantus.logger.Logger;


/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class WavUtils {

    private static Logger log = Logger.getLogger(WavUtils.class);

    public static final double MIN_POSITIVE_SHORT = 1.0 / Short.MAX_VALUE; // 0x7fffor32767

    public static final int SMOOTH_MOVING_AVERAGE = 0;

    public static final int SMOOTH_SAVITZKY_GOLAY = 1;

    public static final int SMOOTH_ENSEMBLE_AVERAGE = 2;

    public static final int SMOOTH_GAUSIAN = 3;

    public static float[] SG = new float[13];
    static {
        //		467 0,165485471
        //		462 0,163713678
        //		447 0,158398299
        //		422 0,149539334
        //		387 0,137136782
        //		343 0,121545004
        //		287 0,101700921
        //		222 0,078667612
        //		147 0,052090716
        //		62 0,021970234
        //		-33 -0,011693834
        //		-138 -0,048901488
        //		-253 -0,089652729
        //		2822

        SG[0] = 0.165485471f;
        SG[1] = 0.163713678f;
        SG[2] = 0.158398299f;
        SG[3] = 0.149539334f;
        SG[4] = 0.137136782f;
        SG[5] = 0.121545004f;
        SG[6] = 0.101700921f;
        SG[7] = 0.078667612f;
        SG[8] = 0.052090716f;
        SG[9] = 0.021970234f;
        SG[10] = -0.011693834f;
        SG[11] = -0.048901488f;
        SG[12] = -0.089652729f;

    }


    public static int btoi(byte[] in) {
        return ((in[3] & 0xff) << 24) | ((in[2] & 0xff) << 16)
                | ((in[1] & 0xff) << 8) | (in[0] & 0xff);
    }

    public static short btos(byte[] in) {
        return (short) (((in[1] & 0xff) << 8) | (in[0] & 0xff));
    }

    public static short bgtolt(short dt) {
        return (short) ((dt & 0xff) << 8 | (dt & 0xff) >> 8);
    }

    public static boolean isPowerOfTwo(int x) {
        String s = binaryNumber(x);
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i) != '0')
                return false;
        return true;
    }

    public static String binaryNumber(int i) {
        if (i == 0)
            return "0";
        else if (i == 1)
            return "1";
        else
            return binaryNumber(i / 2) + (i % 2);
    }

    public static float[] smoothing(float[] signal) {
        int offset = 1;
        int windowSize = signal.length / 10;//1024;
        return smoothing(signal, windowSize, offset, SMOOTH_MOVING_AVERAGE);

    }

    public static float[] smoothing(float[] signal, int windowSize, int offset,
            int method) {
        if (signal == null) {
            log.error("[smoothing] Error! signal is null");
            return null;
        }
        int halfWindowSize = windowSize / 8;
        int singnalLength = signal.length;

        switch (method) {

        case SMOOTH_MOVING_AVERAGE:
            offset = offset + halfWindowSize;
            for (int i = 1 + offset; i <= singnalLength - offset; i++) {
                float d1 = 0.0f;
                for (int i1 = -halfWindowSize; i1 <= halfWindowSize; i1++)
                    d1 += signal[i + i1];

                signal[i] = d1 / (float) (2 * halfWindowSize + 1);
            }
            return signal;

        case SMOOTH_SAVITZKY_GOLAY:
            int k1 = 12;

            offset = offset + k1;

            for (int j = 1 + offset; j <= singnalLength - offset; j++) {
                float d2 = 0.0f;
                for (int j1 = 1; j1 <= k1; j1++) {
                    d2 += SG[j1] * (signal[j - j1] + signal[j + j1]);
                    //d2 += 100 * (signal[j - j1] + signal[j + j1]);
                }
                signal[j] = SG[0] * signal[j] + d2;
                //signal[j] = 190 * signal[j] + d2;
            }
            return signal;
        //			return null;
        case SMOOTH_ENSEMBLE_AVERAGE:

            //            if(Noi == 1)
            float d3 = 4f;
            //            if(Noi == 2)
            //                d3 = 10D;
            //            if(Noi == 3)
            //                d3 = 24D;
            for (int k = 1; k <= 1000; k++)
                //signal[k] = signal[k] + (F0[k] + Nnoise(d3, 0.0f));
                signal[k] = signal[k] + (10 + Nnoise(d3, 0.0f));
            //
            //            for(int l = 1; l <= 1000; l++)
            //            	signal[l] = signal[l] / (float)Npass;

            log.error("SMOOTH_ENSEMBLE_AVERAGE: NOt implemented");
            return null;
        case SMOOTH_GAUSIAN:
            return smoothGausian(signal, windowSize);

        }
        return null;
    }

    static float Nnoise(float d, float d1) {
        float d2 = 0.0f;
        float d3 = 0.0f;
        float d4 = 0.0f;
        float d5 = 0.0f;
        float d6 = 0.0f;
        do {
            d4 = (float) (2D * Math.random() - 1.0D);
            d5 = (float) (2D * Math.random() - 1.0D);
            d3 = d4 * d4 + d5 * d5;
        } while (d3 >= 1.0D);
        d2 = (float) (Math.sqrt((-2D * Math.log(d3)) / d3));
        d6 = d5 * d2;
        return d1 + d * d6;
    }

    public static float[] smoothGausian(float inPixels[], double sigma) {
        //		float outPixels[] = new float[inPixels.length];
        //		/float temp[] = new float[inPixels.length];
        int windowSize = (int) (sigma * 3.0);
        int offset = (windowSize - 1) / 2;
        double weight[] = new double[windowSize];
        double s = -1.5 * sigma;

        // Calculate weights and put them in weight[] array

        for (int i = 0; i < windowSize; i++, s += 3.0 * sigma
                / ((double) windowSize - 1.0)) {
            weight[i] = 1.0 / (Math.sqrt(2.0 * Math.PI) * sigma)
                    * Math.exp(-1.0 * s * s / (2.0 * sigma * sigma));
        }
        // Smooth along the x axis, result in temp[] array

        for (int y = offset; y < inPixels.length - offset; y++) {
            for (int i = y - offset, k = 0; i <= y + offset; i++, k++) {
                inPixels[y] += (inPixels[y] * weight[k]);//(float) (weight[k] *
                                                         // inPixels[y]);
                //log.debug("dmn[" + k + "]: " + weight[k] );
            }
        }
        // Smooth along the y axis

        //		for (int y = offset; y < size - offset; y++){
        //			for (int j = y - offset, k = 0; j <= y + offset; j++, k++){
        //				inPixels[y] = (float) (weight[y] * inPixels[y]);
        //				//log.debug("outPixels[y]" + outPixels[y]);
        //
        //			}
        //
        //		}

        return inPixels;
    }

 

    /**
     *  
     */
    public static String convertArray(float[] numArray) {
        //	        log.debug("[convertArray(float)]numArray.length: " +
        // numArray.length);
        if(numArray == null){
            return "[]";
        }
        StringBuilder strArray = new StringBuilder();
        strArray.append("[");
        String seperator = "";
        NumberFormat nf = NumberFormat.getInstance();
        for (int i = 0; i < numArray.length; i++) {
            float f = numArray[i];
            strArray.append(seperator);
            strArray.append(nf.format(f));

            seperator = "; ";
        }
        strArray.append("]");
        log.debug("[convertArray(float)]strArray: " + strArray.toString());
        return strArray.toString();
    }

    public static float[] convertArray(String strArray) throws ParseException {
        //	        log.debug("[convertArray(String)]strArray: " + strArray);
        String lStrArrayTruncated = strArray
                .substring(1, strArray.length() - 1);
        //	        log.debug("[convertArray(String)]lStrArrayTruncated: " +
        // lStrArrayTruncated);
        String[] lStrArraySplited = lStrArrayTruncated.split("; ");
        //	        log.debug("[convertArray(String)] lStrArraySplited.length: " +
        // lStrArraySplited.length);
        NumberFormat nf = NumberFormat.getInstance();
        float[] numArray = new float[lStrArraySplited.length];
        for (int i = 0; i < lStrArraySplited.length; i++) {
            String str = lStrArraySplited[i];
            Number number = new Float(0);
            if("".equals(str)){
                continue;
            }else{
                number = nf.parse(str);
            }
            number = nf.parse(str);
            numArray[i] = number.floatValue();
        }
        return numArray;

    }
    public static String display(float f){
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(f);
    }
    public static String display(double f){
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(f);
    }

}