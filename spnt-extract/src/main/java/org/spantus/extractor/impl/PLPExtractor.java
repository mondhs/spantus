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
import org.spantus.math.services.MathServicesFactory;
import org.spantus.math.services.PLPService;

/**
 * Mel-frequency cepstral coefficients feature value extractor
 *
 * @author Mindaugas Greibus
 * @since 0.0.1
 *        <p/>
 *        Created 2008.02.29
 */
public class PLPExtractor extends AbstractSpectralVectorExtractor {
    static Logger log = Logger.getLogger(PLPExtractor.class);

    private PLPService plpService;


    public int getDimension() {
        return 13;
    }

    public String getName() {
        return ExtractorEnum.PLP_EXTRACTOR.name();
    }


    public FrameVectorValues calculateWindow(FrameValues window) {
        FrameVectorValues spectrum = calculateFFT(window);
        FrameVectorValues calculatedValues = super.calculateWindow(window);
        for (List<Double> spectra : spectrum) {
        	try{
            List<Double> mfcc = getPlpService().calculateFromSpectrum(spectra,
                    getConfig().getSampleRate());
            calculatedValues.add(mfcc);
        	}catch (IllegalArgumentException e) {
        		getAbstractExtractorVector().flush();
        		throw e;
        	}
        }

        return calculatedValues;
    }

	public PLPService getPlpService() {
		if(plpService == null){
			plpService = MathServicesFactory.createPLPService();
		}
		return plpService;
	}

	public void setPlpService(PLPService plpService) {
		this.plpService = plpService;
	}

//    public PLPService getMfccService() {
//        if (mfccService == null) {
//            mfccService = MathServicesFactory.createMFCCService();
//        }
//        return mfccService;
//    }
//
//    public void setMfccService(MFCCService mfccService) {
//        this.mfccService = mfccService;
//    }

}
