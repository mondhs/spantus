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
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public enum ExtractorEnum {
	FFT_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector, "FFT"),
	ENERGY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Energy"),
	NOISE_LEVEL_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Noise Level"),
	LPC_RESIDUAL_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "LPC Residual"),
	SIGNAL_ENTROPY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Signal Entropy"),
	LOUDNESS_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Loudness"),
	PEAK_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Peak"),
	CROSSING_ZERO_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Crossing Zero"),
	SIGNAL_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Signal"),
	LOG_ATTACK_TIME(ExtractorTypeEnum.SequenceOfScalar, "Log Attact"),
	WAVFORM_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector,"Wavform"),
	LPC_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector, "LPC"),
	MFCC_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector,"MFCC"),
	PLP_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector,"PLP"),
	AUTOCORRELATION_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Autocorrelation"),
	ENVELOPE_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar,"Envelope"),
	SPECTRAL_CENTROID_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Speactral Centroid"),
	SPECTRAL_FLUX_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Spectral Flux"),
	SPECTRAL_ENTROPY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Spectral Entoropy"),
	SPECTRUM_POWER_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar, "Spectrum Power"),
//	HarmonicProductSpectrum(ExtractorTypeEnum.SequenceOfScalar),
	SPECTRAL_GAIN_FACTOR(ExtractorTypeEnum.SequenceOfVector, "Spegtral Gain Factor")
	;
	
	ExtractorEnum(ExtractorTypeEnum type, String displayName) {
		this.type = type;
		this.displayName = displayName;
	}
	
	ExtractorTypeEnum type;
	String displayName;
	public ExtractorTypeEnum getType(){
		return type;
	}
	public String getDisplayName() {
		return displayName;
	}
}
