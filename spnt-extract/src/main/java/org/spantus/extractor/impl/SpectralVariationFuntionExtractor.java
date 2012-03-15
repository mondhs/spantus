package org.spantus.extractor.impl;

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
/**
 * inproceedings{sharma1996blind,
 * title={“Blind” speech segmentation: automatic segmentation of speech without linguistic knowledge},
 * author={Sharma, M. and Mammone, R.},
 * booktitle={Spoken Language, 1996. ICSLP 96. Proceedings., Fourth International Conference on},
 * volume={2},
 * pages={1237--1240},
 * year={1996},
 * organization={IEEE}
}
 * 
 * @author mgreibus
 *
 */
public class SpectralVariationFuntionExtractor extends
		AbstractSpectralExtractor {
	
	public SpectralVariationFuntionExtractor() {
		setAbstractExtractorVector(new MFCCExtractor());
	}

	public String getName() {
		return ExtractorEnum.SPECTRAL_VARIATION_FUNCTION_EXTRACTOR.name();
	}
	

	public FrameValues calculateWindow(FrameValues window) {
		FrameVectorValues val3d = calculateFFT(window);
		FrameValues rtnValues = super.calculateWindow(window);
		for (List<Double> fv : val3d) {
			Double previousAbs = null;
			Double flux = 0D;
			for (Double current : fv) {
				if (previousAbs == null) {
					previousAbs = Math.abs(current);
					continue;
				}
				// x=(|X[k]|-|X[k-1]|)
				Double x = Math.abs(current) - previousAbs;
				// H(x)=(x+|x|)/2
				flux += (x + Math.abs(x)) / 2;
				previousAbs = Math.abs(current);
			}
			// Normalization
			flux /= fv.size();
			rtnValues.add(flux);
		}
		return rtnValues;
	}

}
