/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
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
	FFT_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector),
	ENERGY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	SMOOTHED_ENERGY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	SIGNAL_ENTROPY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	LOUDNESS_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	CROSSING_ZERO_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	SIGNAL_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	LOG_ATTACK_TIME(ExtractorTypeEnum.SequenceOfScalar),
	SMOOTHED_LOG_ATTACK_TIME(ExtractorTypeEnum.SequenceOfScalar),
	WAVFORM_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector),
	LPC_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector),
	MFCC_EXTRACTOR(ExtractorTypeEnum.SequenceOfVector),
	AUTOCORRELATION_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	ENVELOPE_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	SPECTRAL_CENTROID_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	SPECTRAL_FLUX_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar),
	SPECTRAL_ENTROPY_EXTRACTOR(ExtractorTypeEnum.SequenceOfScalar)
	;
	
	ExtractorEnum(ExtractorTypeEnum type) {
		this.type = type;
	}
	
	ExtractorTypeEnum type;
	public ExtractorTypeEnum getType(){
		return type;
	}
}
