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

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.logger.Logger;
import org.spantus.math.services.MFCCService;
import org.spantus.math.services.MathServicesFactory;

/**
 * Mel-frequency cepstral coefficients feature value extractor
 *
 * @author Mindaugas Greibus
 * @since 0.0.1
 *        <p/>
 *        Created 2008.02.29
 */
public class MFCCExtractor extends AbstractSpectralVectorExtractor {
    static Logger log = Logger.getLogger(MFCCExtractor.class);

    private MFCCService mfccService;


    public int getDimension() {
        return 13;
    }

    public String getName() {
        return ExtractorEnum.MFCC_EXTRACTOR.name();
    }


    public FrameVectorValues calculateWindow(FrameValues window) {
//        FrameVectorValues spectrum = calculateFFT(window);
//        spectrum.setSampleRate(getExtractorSampleRate());
        FrameVectorValues calculatedValues = super.calculateWindow(window);
//        for (List<Double> spectra : spectrum) {
//        	try{
//            List<Double> mfcc = getMfccService().calculateMfccFromSpectrum(window, spectra,
//                    getConfig().getSampleRate());
//            
//            calculatedValues.add(mfcc);
//        	}catch (IllegalArgumentException e) {
//        		getAbstractExtractorVector().flush();
//        		throw e;
//        	}
//        }
        List<Double> mfcc = getMfccService().calculateMFCC(window, 
                    getConfig().getSampleRate());
        calculatedValues.add(mfcc);


        return calculatedValues;
    }

    public MFCCService getMfccService() {
        if (mfccService == null) {
            mfccService = MathServicesFactory.createMFCCService();
        }
        return mfccService;
    }

    public void setMfccService(MFCCService mfccService) {
        this.mfccService = mfccService;
    }

}
