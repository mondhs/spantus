/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.extractor.impl;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.core.threshold.DynamicThreshold;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.threshold.ExtremeClassifier;
import org.spantus.core.threshold.OfflineThreshold;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.core.threshold.ThresholdEnum;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorResultBufferFactory;
import org.spantus.extractor.modifiers.DeltaExtractor;
import org.spantus.extractor.modifiers.LogExtractor;
import org.spantus.extractor.modifiers.MeanExtractor;
import org.spantus.extractor.modifiers.SmoothedExtractor;
import org.spantus.extractor.modifiers.StdevExtractor;
import org.spantus.logger.Logger;
import org.spantus.utils.ExtractorParamUtils;

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

        private static Logger log = Logger.getLogger(ExtractorUtils.class);
	static Map<ExtractorEnum, Class<? extends AbstractExtractor>> extractorMap = new HashMap<ExtractorEnum, Class<? extends AbstractExtractor>>();
	static Map<ExtractorEnum, Class<? extends AbstractExtractor3D>> extractor3DMap = new HashMap<ExtractorEnum, Class<? extends AbstractExtractor3D>>();

	static {
		extractorMap.put(ExtractorEnum.SIGNAL_EXTRACTOR, SignalExtractor.class);
		extractorMap.put(ExtractorEnum.ENERGY_EXTRACTOR, EnergyExtractor.class);
		extractorMap.put(ExtractorEnum.PEAK_EXTRACTOR, PeakExtractor.class);

//		extractorMap.put(ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR,
//				EnergyExtractor.class);
		extractorMap.put(ExtractorEnum.LPC_RESIDUAL_EXTRACTOR,
				LPCResidualExtractor.class);
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
		extractorMap.put(ExtractorEnum.NOISE_LEVEL_EXTRACTOR,
				NoiseLevelExtractor.class);
		extractorMap.put(ExtractorEnum.SPECTRUM_POWER_EXTRACTOR,
				SpectrumPower.class);
		extractorMap.put(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
				SpectralFlux.class);
//		extractorMap.put(ExtractorEnum.HarmonicProductSpectrum,
//				HarmonicProductSpectrum.class);
		extractorMap.put(ExtractorEnum.LOUDNESS_EXTRACTOR, Loudness.class);
		extractorMap.put(ExtractorEnum.LOG_ATTACK_TIME,
				LogAttackTimeExtractor.class);
//		extractorMap.put(ExtractorEnum.SMOOTHED_LOG_ATTACK_TIME,
//				LogAttackTimeExtractor.class);
		
		extractor3DMap.put(ExtractorEnum.WAVFORM_EXTRACTOR,
				WavformExtractor.class);
		extractor3DMap.put(ExtractorEnum.FFT_EXTRACTOR, FFTExtractor.class);
		extractor3DMap.put(ExtractorEnum.LPC_EXTRACTOR, LPCExtractor.class);
		extractor3DMap.put(ExtractorEnum.MFCC_EXTRACTOR, MFCCExtractor.class);
		extractor3DMap.put(ExtractorEnum.SPECTRAL_GAIN_FACTOR,
				SpectralGainFactorExtractor.class);


	}

	/**
	 * 
	 * @param bufferedReader
	 * @param extractorType
	 */
	public static void register(IExtractorInputReader bufferedReader,
			ExtractorEnum extractorType, ExtractorParam param) {
		bufferedReader.registerExtractor(ExtractorResultBufferFactory
				.create(createInstance(extractorType, param)));
	}

	public static IGeneralExtractor createInstance(ExtractorEnum extractor, ExtractorParam param) {
		try {
			if (extractorMap.get(extractor) != null) {
				IExtractor extractorInstance = extractorMap.get(extractor)
						.newInstance();
				if(ExtractorParamUtils.getBoolean(param, 
						ExtractorModifiersEnum.delta.name(), false)){
					DeltaExtractor delta = new DeltaExtractor();
					delta.setExtractor(extractorInstance);
					extractorInstance = delta;
				}
				if(ExtractorParamUtils.getBoolean(param, 
						ExtractorModifiersEnum.mean.name(), false)){
					MeanExtractor mean = new MeanExtractor();
					mean.setExtractor(extractorInstance);
					extractorInstance = mean;
				}
				if(ExtractorParamUtils.getBoolean(param, 
						ExtractorModifiersEnum.stdev.name(), false)){
					StdevExtractor stdev = new StdevExtractor();
					stdev.setExtractor(extractorInstance);
					extractorInstance = stdev;
				}
				if(ExtractorParamUtils.getBoolean(param, 
						ExtractorModifiersEnum.smooth.name(), false)){
					SmoothedExtractor smooted = new SmoothedExtractor();
					smooted.setExtractor(extractorInstance);
					extractorInstance = smooted;
				}
				if(ExtractorParamUtils.getBoolean(param, 
						ExtractorModifiersEnum.log.name(), false)){
					LogExtractor logExtactor = new LogExtractor();
					logExtactor.setExtractor(extractorInstance);
					extractorInstance = logExtactor;
				}
				return extractorInstance;
			} else if (extractor3DMap.get(extractor) != null) {
				return extractor3DMap.get(extractor).newInstance();
			} else {
				throw new RuntimeException("Not impl");
			}
		} catch (InstantiationException e) {
                        log.error(e);
		} catch (IllegalAccessException e) {
			log.error(e);
		}
		return null;

	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractorType
	 * @param param
	 * @param threshold
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader bufferedReader, ExtractorEnum extractorType,
			ExtractorParam param,
			AbstractClassifier threshold) {
		IGeneralExtractor generalExtr = createInstance(extractorType, param);
		if (generalExtr instanceof IExtractor) {
			ExtractorWrapper wraper = new ExtractorWrapper(
					(IExtractor) generalExtr);
			threshold.setExtractor(new ExtractorResultBuffer(wraper));
			wraper.getListeners().add(threshold);
			bufferedReader.registerExtractor(threshold);
		} else {
			register(bufferedReader, extractorType, param);
		}
		return threshold;

	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractorType
	 * @param param
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader bufferedReader, ExtractorEnum extractorType, ExtractorParam param) {
		return registerThreshold(bufferedReader, extractorType, param, ThresholdEnum.online);
	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractorType
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader bufferedReader, ExtractorEnum extractorType) {
		if(extractor3DMap.get(extractorType)!=null){
			register(bufferedReader, extractorType, null);
			return null;
		}else{
			return registerThreshold(bufferedReader, extractorType, null, ThresholdEnum.offline);
		}
	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractors
	 * @param params
	 */
	public static void registerThreshold(IExtractorInputReader bufferedReader,
			ExtractorEnum[] extractors,Map<String, ExtractorParam> params) {
		registerThreshold(bufferedReader, extractors, params, ThresholdEnum.online);
	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractorType
	 * @param param
	 * @param thresholdType
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader bufferedReader, ExtractorEnum extractorType,
			ExtractorParam param,
			ThresholdEnum thresholdType) {
		AbstractClassifier threshold = null;
		
		switch (thresholdType) {
		case online:
			threshold = new StaticThreshold();
			break;
		case dynamic:
			threshold = new DynamicThreshold();
			break;
		case offline:
			threshold = new OfflineThreshold();
			break;
		case rules:
			threshold = new ExtremeClassifier();
		default:
			break;
		}
		return registerThreshold(bufferedReader, extractorType, param,
				threshold);
	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractors
	 * @param params
	 * @param thresholdType
	 */
	public static void registerThreshold(IExtractorInputReader bufferedReader,
			ExtractorEnum[] extractors,
			Map<String, ExtractorParam> params,
			ThresholdEnum thresholdType) {
		for (ExtractorEnum extractor : extractors) {
			ExtractorParam extractorParam = null;
			if(params!=null){
				extractorParam = params.get(extractor.name());
			}
			ExtractorUtils.registerThreshold(bufferedReader, extractor, 
					extractorParam
					, thresholdType);
		}
	}
	/**
	 * 
	 * @param bufferedReader
	 * @param extractors
	 * @param params
	 */
	public static void register(IExtractorInputReader bufferedReader,
			ExtractorEnum[] extractors,Map<String, ExtractorParam> params) {
		for (ExtractorEnum extractor : extractors) {
			ExtractorUtils.register(bufferedReader, extractor, 
					ExtractorParamUtils.getSafeParam(params, extractor.name())
					);
		}
	}
}
