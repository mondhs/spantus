 /*
 * Copyright 1999-2002 Carnegie Mellon University.
 * Portions Copyright 2002 Sun Microsystems, Inc.
 * Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.spantus.frontend.feature;

import java.util.LinkedList;
import java.util.List;


/**
 * Applies cepstral mean normalization (CMN), sometimes called channel mean normalization, to incoming cepstral data.
 * Its goal is to reduce the distortion caused by the transmission channel.  The output is mean normalized cepstral
 * data.
 * <p/>
 * The CMN processing subtracts the mean from all the  Data objects between a
 * edu.cmu.sphinx.frontend.DataStartSignal and a DataEndSignal or between a
 * edu.cmu.sphinx.frontend.endpoint.SpeechStartSignal and a  SpeechEndSignal.  BatchCMN will read in all the Data
 * objects, calculate the mean, and subtract this mean from all the  Data objects. For a given utterance, it will
 * only produce an output after reading all the incoming data for the utterance. As a result, this process can introduce
 * a significant processing delay, which is acceptable for batch processing, but not for live mode. In the latter case,
 * one should use the   LiveCMN.
 * <p/>
 * CMN is a technique used to reduce distortions that are introduced by the transfer function of the transmission
 * channel (e.g., the microphone). Using a transmission channel to transmit the input speech translates to multiplying
 * the spectrum of the input speech with the transfer function of the channel (the distortion).  Since the cepstrum is
 * the Fourier Transform of the log spectrum, the logarithm turns the multiplication into a summation. Averaging over
 * time, the mean is an estimate of the channel, which remains roughly constant. The channel is thus removed from the
 * cepstrum by subtracting the mean cepstral vector. Intuitively, the mean cepstral vector approximately describes the
 * spectral characteristics of the transmission channel (e.g., microphone).
 *
 * see LiveCMN
 */
public class BatchCMN  {

    private Double[] sums;           // array of current sums
    private List<Double[]> cepstraList;
    private int numberDataCepstra;

    public BatchCMN() {
//        initLogger();
    }

    /* (non-Javadoc)
     * @see edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util.props.PropertySheet)
     */
//    @Override
//    public void newProperties(PropertySheet ps) throws PropertyException {
//        super.newProperties(ps);
//    }


    /** Initializes this BatchCMN. */
//    @Override
    public void initialize() {
//        super.initialize();
        sums = null;
        cepstraList = new LinkedList<Double[]>();
    }


    /** Initializes the sums array and clears the cepstra list. */
//    private void reset() {
//        sums = null; // clears the sums array
//        cepstraList.clear();
//        numberDataCepstra = 0;
//    }


    /**
     * Returns the next Data object, which is a normalized cepstrum. Signal objects are returned unmodified.
     *
     * @return the next available Data object, returns null if no Data object is available
     */
//    @Override
//    public Data getData() throws DataProcessingException {
//
//        Data output = null;
//
//        if (!cepstraList.isEmpty()) {
//            output = cepstraList.remove(0);
//        } else {
//            reset();
//            // read the cepstra of the entire utterance, calculate
//            // and apply the cepstral mean
//            if (readUtterance() > 0) {
//                normalizeList();
//                output = cepstraList.remove(0);//getData();
//            }
//        }
//
//        return output;
//    }

    public Double[] process(Double[] value){
    	Double[] output = null;
        if (!cepstraList.isEmpty()) {
            output = cepstraList.remove(0);
        } else {
//            reset();
            // read the cepstra of the entire utterance, calculate
            // and apply the cepstral mean
            if (readUtterance(value) > 0) {
                normalizeList();
                output = cepstraList.remove(0);//getData();
            }
        }
        return output;
    }

    /**
     * Reads the cepstra of the entire Utterance into the cepstraList.
     *
     * @return the number cepstra (with Data) read
     */
    private int readUtterance(Double[] input) {
        numberDataCepstra++;
        Double[] cepstrumData = input;
        if (sums == null) {
            sums = new Double[cepstrumData.length];
        } else {
            if (sums.length != cepstrumData.length) {
                throw new Error
                        ("Inconsistent cepstrum lengths: sums: " +
                                sums.length + ", cepstrum: " +
                                cepstrumData.length);
            }
        }
        // add the cepstrum data to the sums
        for (int j = 0; j < cepstrumData.length; j++) {
        	if(sums[j]==null){
        		sums[j] = 0D; 
        	}
            sums[j] += cepstrumData[j];
        }
        cepstraList.add(input);


        return numberDataCepstra;
    }


    /** Normalizes the list of Data. */
    private void normalizeList() {

        // calculate the mean first
        for (int i = 0; i < sums.length; i++) {
            sums[i] /= numberDataCepstra;
        }

        for (Double[] data : cepstraList) {
        	Double[] cepstrum = data;
                for (int j = 0; j < cepstrum.length; j++) {
                    cepstrum[j] -= sums[j]; // sums[] is now the means[]
                }
         }
    }


}