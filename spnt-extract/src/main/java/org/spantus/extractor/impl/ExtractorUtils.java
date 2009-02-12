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

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.core.threshold.IThreshold;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorResultBufferFactory;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.04.18
 * 
 */
public abstract class ExtractorUtils {

	static Map<ExtractorEnum, Class<? extends AbstractExtractor>> extractorMap = new HashMap<ExtractorEnum, Class<? extends AbstractExtractor>>();
	static Map<ExtractorEnum, Class<? extends AbstractExtractor3D>> extractor3DMap = new HashMap<ExtractorEnum, Class<? extends AbstractExtractor3D>>();

	static {
		extractorMap.put(ExtractorEnum.SIGNAL_EXTRACTOR, SignalExtractor.class);
		extractorMap.put(ExtractorEnum.ENERGY_EXTRACTOR, EnergyExtractor.class);
		extractorMap.put(ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR,
				EnergyExtractor.class);
		extractorMap.put(ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
				SignalEntropyExtractor.class);
		extractorMap.put(ExtractorEnum.CROSSING_ZERO_EXTRACTOR,
				CrossingZeroExtractor.class);
		extractorMap.put(ExtractorEnum.ENVELOPE_EXTRACTOR,
				EnvelopeExtractor.class);
		extractorMap.put(ExtractorEnum.AUTOCORRELATION_EXTRACTOR,
				AutocorrelationExtractor.class);
		extractorMap.put(ExtractorEnum.SPECTRAL_CENTROID_EXTRACTOR,
				SpectralCentroid.class);
		extractorMap.put(ExtractorEnum.SPECTRAL_ENTROPY_EXTRACTOR,
				SpectralEntropy.class);
		extractorMap.put(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
				SpectralFlux.class);
		extractorMap.put(ExtractorEnum.LOUDNESS_EXTRACTOR, Loudness.class);
		extractorMap.put(ExtractorEnum.LOG_ATTACK_TIME,
				LogAttackTimeExtractor.class);
		extractorMap.put(ExtractorEnum.SMOOTHED_LOG_ATTACK_TIME,
				LogAttackTimeExtractor.class);

		extractor3DMap.put(ExtractorEnum.WAVFORM_EXTRACTOR,
				WavformExtractor.class);
		extractor3DMap.put(ExtractorEnum.FFT_EXTRACTOR, FFTExtractor.class);
		extractor3DMap.put(ExtractorEnum.LPC_EXTRACTOR, LPCExtractor.class);
		extractor3DMap.put(ExtractorEnum.MFCC_EXTRACTOR, MFCCExtractor.class);

	}

	/**
	 * 
	 * @param bufferedReader
	 * @param extractor
	 */
	public static void register(IExtractorInputReader bufferedReader,
			ExtractorEnum extractor) {
		bufferedReader.registerExtractor(ExtractorResultBufferFactory
				.create(createInstance(extractor)));
	}

	public static IGeneralExtractor createInstance(ExtractorEnum extractor) {
		try {
			if (extractorMap.get(extractor) != null) {
				IExtractor extractorInstance = extractorMap.get(extractor)
						.newInstance();
				if (extractor.name().startsWith("SMOOTHED_")) {
					SmoothedExtractor smooted = new SmoothedExtractor();
					smooted.setExtractor(extractorInstance);
					extractorInstance = smooted;
				}
				return extractorInstance;
			} else if (extractor3DMap.get(extractor) != null) {
				return extractor3DMap.get(extractor).newInstance();
			} else {
				throw new RuntimeException("Not impl");
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static IThreshold registerThreshold(
			IExtractorInputReader bufferedReader, ExtractorEnum extractorType,
			AbstractThreshold threshold) {
		IGeneralExtractor generalExtr = createInstance(extractorType);
		if (generalExtr instanceof IExtractor) {
			ExtractorWrapper wraper = new ExtractorWrapper(
					(IExtractor) generalExtr);
			threshold.setExtractor(new ExtractorResultBuffer(wraper));
			wraper.getListeners().add(threshold);
			bufferedReader.registerExtractor(threshold);
		} else {
			register(bufferedReader, extractorType);
		}
		return threshold;

	}

	public static IThreshold registerThreshold(
			IExtractorInputReader bufferedReader, ExtractorEnum extractorType) {
		return registerThreshold(bufferedReader, extractorType,
				new StaticThreshold());
	}

	public static void registerThreshold(IExtractorInputReader bufferedReader,
			ExtractorEnum[] extractors) {
		for (ExtractorEnum extractor : extractors) {
			ExtractorUtils.registerThreshold(bufferedReader, extractor);
		}
	}

	public static void register(IExtractorInputReader bufferedReader,
			ExtractorEnum[] extractors) {
		for (ExtractorEnum extractor : extractors) {
			ExtractorUtils.register(bufferedReader, extractor);
		}
	}
}
