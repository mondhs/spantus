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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.ConvexHullThreshold;
import org.spantus.core.threshold.DynamicThreshold;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.threshold.OfflineThreshold;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorResultBufferFactory;
import org.spantus.extractor.modifiers.DeltaExtractor;
import org.spantus.extractor.modifiers.LogExtractor;
import org.spantus.extractor.modifiers.MeanExtractor;
import org.spantus.extractor.modifiers.SmoothedExtractor;
import org.spantus.extractor.modifiers.StdevExtractor;
import org.spantus.extractor.segments.online.ExtremeOfflineRuleClassifier;
import org.spantus.extractor.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
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
public final class ExtractorUtils {
	private ExtractorUtils() {
	}

	private static Logger log = Logger.getLogger(ExtractorUtils.class);
	static Map<ExtractorEnum, Class<? extends AbstractExtractor>> extractorMap = new HashMap<ExtractorEnum, Class<? extends AbstractExtractor>>();
	static Map<ExtractorEnum, Class<? extends AbstractExtractorVector>> extractor3DMap = new HashMap<ExtractorEnum, Class<? extends AbstractExtractorVector>>();

	static {
		extractorMap.put(ExtractorEnum.SIGNAL_EXTRACTOR, SignalExtractor.class);
		extractorMap.put(ExtractorEnum.ENERGY_EXTRACTOR, EnergyExtractor.class);
		extractorMap.put(ExtractorEnum.PEAK_EXTRACTOR, PeakExtractor.class);

		// extractorMap.put(ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR,
		// EnergyExtractor.class);
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
		// extractorMap.put(ExtractorEnum.HarmonicProductSpectrum,
		// HarmonicProductSpectrum.class);
		extractorMap.put(ExtractorEnum.LOUDNESS_EXTRACTOR, Loudness.class);
		extractorMap.put(ExtractorEnum.LOG_ATTACK_TIME,
				LogAttackTimeExtractor.class);

		extractor3DMap.put(ExtractorEnum.WAVFORM_EXTRACTOR,
				WavformExtractor.class);
		extractor3DMap.put(ExtractorEnum.FFT_EXTRACTOR,
				FFTExtractorCached.class);
		extractor3DMap.put(ExtractorEnum.LPC_EXTRACTOR, LPCExtractor.class);
		extractor3DMap.put(ExtractorEnum.MFCC_EXTRACTOR, MFCCExtractor.class);
		extractor3DMap.put(ExtractorEnum.DELTA_MFCC_EXTRACTOR,
				DeltaMFCCExtractor.class);
		extractor3DMap.put(ExtractorEnum.DELTA_DELTA_MFCC_EXTRACTOR,
				DeltaDeltaMFCCExtractor.class);
		extractor3DMap.put(ExtractorEnum.PLP_EXTRACTOR, PLPExtractor.class);
		extractor3DMap.put(ExtractorEnum.SPECTRAL_GAIN_FACTOR,
				SpectralGainFactorExtractor.class);
		extractorMap.put(ExtractorEnum.LPC_ERROR_EXTRACTOR,
				LPCErrorExtractor.class);
		extractorMap.put(ExtractorEnum.SPECTRAL_VARIATION_FUNCTION_EXTRACTOR,
				SpectralVariationFuntionExtractor.class);

	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractorType
	 * @return
	 */
	public static IGeneralExtractor<?> register(
			IExtractorInputReader extractorReader, ExtractorEnum extractorType,
			ExtractorParam param) {
		IGeneralExtractor<?> extractor = ExtractorResultBufferFactory
				.create(createInstance(extractorType, param));
		extractorReader.registerExtractor(extractor);
		return extractor;
	}

	public static IGeneralExtractor<?> createInstance(ExtractorEnum extractor,
			ExtractorParam param) {
		try {
			if (extractorMap.get(extractor) != null) {
				IExtractor extractorInstance = extractorMap.get(extractor)
						.newInstance();
				if (ExtractorParamUtils.getValue(param,
						ExtractorModifiersEnum.delta.name(), false)) {
					DeltaExtractor delta = new DeltaExtractor();
					delta.setExtractor(extractorInstance);
					extractorInstance = delta;
				}
				if (ExtractorParamUtils.getValue(param,
						ExtractorModifiersEnum.mean.name(), false)) {
					MeanExtractor mean = new MeanExtractor();
					mean.setExtractor(extractorInstance);
					extractorInstance = mean;
				}
				if (ExtractorParamUtils.getValue(param,
						ExtractorModifiersEnum.stdev.name(), false)) {
					StdevExtractor stdev = new StdevExtractor();
					stdev.setExtractor(extractorInstance);
					extractorInstance = stdev;
				}
				if (ExtractorParamUtils.getValue(param,
						ExtractorModifiersEnum.smooth.name(), false)) {
					SmoothedExtractor smooted = new SmoothedExtractor();
					smooted.setExtractor(extractorInstance);
					extractorInstance = smooted;
				}
				if (ExtractorParamUtils.getValue(param,
						ExtractorModifiersEnum.log.name(), false)) {
					LogExtractor logExtactor = new LogExtractor();
					logExtactor.setExtractor(extractorInstance);
					extractorInstance = logExtactor;
				}
				return extractorInstance;
			} else if (extractor3DMap.get(extractor) != null) {
				return extractor3DMap.get(extractor).newInstance();
			} else {
				throw new RuntimeException("Not impl: " + extractor);
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
	 * @param extractorReader
	 * @param extractorType
	 * @param param
	 * @param threshold
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader extractorReader, ExtractorEnum extractorType,
			ExtractorParam param, AbstractClassifier threshold) {
		IGeneralExtractor<?> generalExtr = createInstance(extractorType, param);
		if (generalExtr instanceof IExtractor) {
			ExtractorWrapper wraper = new ExtractorWrapper(
					(IExtractor) generalExtr);
			threshold.setExtractor(new ExtractorResultBuffer(wraper));
			wraper.getListeners().add(threshold);
			extractorReader.registerExtractor(threshold);
			return threshold;
		} else {
			register(extractorReader, extractorType, param);
			return null;
		}

	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractorType
	 * @param param
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader extractorReader, ExtractorEnum extractorType,
			ExtractorParam param) {
		return registerThreshold(extractorReader, extractorType, param,
				ClassifierEnum.online);
	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractorType
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader extractorReader, ExtractorEnum extractorType) {
		if (extractor3DMap.get(extractorType) != null) {
			register(extractorReader, extractorType, null);
			return null;
		} else {
			return registerThreshold(extractorReader, extractorType, null,
					ClassifierEnum.offline);
		}
	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractors
	 * @param params
	 */
	public static void registerThreshold(IExtractorInputReader extractorReader,
			ExtractorEnum[] extractors, Map<String, ExtractorParam> params) {
		registerThreshold(extractorReader, extractors, params,
				ClassifierEnum.online);
	}

	/**
	 * Apply params
	 * 
	 * @param abstractThreshold
	 * @param param
	 * @return
	 */
	protected static AbstractThreshold applyParams(
			AbstractThreshold abstractThreshold, ExtractorParam param) {
		if (param == null) {
			return abstractThreshold;
		}
		Number threasholdCoef = ExtractorParamUtils.<Double> getValue(param,
				ExtractorParamUtils.commonParam.threasholdCoef.name(), 0.1D);
		abstractThreshold.setCoef(threasholdCoef.doubleValue());
		return abstractThreshold;
	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractorType
	 * @param param
	 * @param thresholdType
	 * @return
	 */
	public static IClassifier registerThreshold(
			IExtractorInputReader extractorReader, ExtractorEnum extractorType,
			ExtractorParam param, ClassifierEnum thresholdType) {
		AbstractClassifier threshold = null;

		switch (thresholdType) {
		case online:
			threshold = applyParams(new StaticThreshold(), param);
			break;
		case dynamic:
			threshold = applyParams(new DynamicThreshold(), param);
			break;
		case offline:
			threshold = applyParams(new OfflineThreshold(), param);
			break;
		case convexHullOffline:
			threshold = applyParams(new ConvexHullThreshold(), param);
			break;
		case rulesOffline:
			// threshold = new ExtremeOnlineRuleClassifier();
			ExtremeOfflineRuleClassifier e2s = new ExtremeOfflineRuleClassifier();
			e2s.setRuleBaseService(ExtremeOnClassifierServiceFactory
					.createClassifierRuleBaseService());
			threshold = e2s;
			break;
		case rulesOnline:
			ExtremeOnlineRuleClassifier eo = new ExtremeOnlineRuleClassifier();
			eo.setRuleBaseService(ExtremeOnClassifierServiceFactory
					.createClassifierRuleBaseService());
			threshold = eo;
			break;
		default:
			break;
		}
		return registerThreshold(extractorReader, extractorType, param,
				threshold);
	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractors
	 * @param params
	 * @param thresholdType
	 */
	public static List<IClassifier> registerThreshold(
			IExtractorInputReader extractorReader, ExtractorEnum[] extractors,
			Map<String, ExtractorParam> params, ClassifierEnum thresholdType) {
		List<IClassifier> classifiers = new ArrayList<IClassifier>();
		for (ExtractorEnum extractor : extractors) {
			ExtractorParam extractorParam = null;
			if (params != null) {
				extractorParam = params.get(extractor.name());
			}
			IClassifier classifier = registerThreshold(
					extractorReader, extractor, extractorParam, thresholdType);
			classifiers.add(classifier);
		}
		return classifiers;
	}

	/**
	 * 
	 * @param extractorReader
	 * @param extractors
	 * @param params
	 */
	public static void register(IExtractorInputReader extractorReader,
			ExtractorEnum[] extractors, Map<String, ExtractorParam> params) {
		for (ExtractorEnum extractor : extractors) {
			ExtractorUtils.register(extractorReader, extractor,
					ExtractorParamUtils.getSafeParam(params, extractor.name()));
		}
	}

	/**
	 * 
	 */
	public static Set<IClassifier> filterOutClassifers(
			IExtractorInputReader extractorReader) {
		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		for (IExtractor extractor : extractorReader.getExtractorRegister()) {
			if (extractor instanceof IClassifier)
				classifiers.add((IClassifier) extractor);
		}
		return classifiers;
	}

	public static FFTExtractor createFftExtractor() {
		return new FFTExtractorCached();
	}
	public static MFCCExtractor createMFCCExtractor() {
		return new MFCCExtractorCached();
	}
}
