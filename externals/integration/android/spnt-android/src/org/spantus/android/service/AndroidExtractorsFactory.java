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
package org.spantus.android.service;

import java.util.HashMap;
import java.util.Map;

import org.spantus.android.dto.ExtractorReaderCtx;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.extractor.impl.FFTExtractor;
import org.spantus.extractor.impl.FFTExtractorCached;
import org.spantus.utils.ExtractorParamUtils;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Jun 3, 2009
 * 
 */
public abstract class AndroidExtractorsFactory {

	public static final Integer DEFAULT_WINDOW_LENGHT = 10;
	public static final Integer DEFAULT_WINDOW_OVERLAP = 33;

	public static IExtractorInputReader createReader(SignalFormat format) {
		ExtractorInputReader reader = new ExtractorInputReader();
		reader.setConfig(createConfig(format));
		return reader;
	}

	/**
	 * 
	 * @return
	 */
	public static ExtractorReaderCtx createDefaultReader() {
		IExtractorConfig extractorConfig = AndroidExtractorConfigUtil
				.defaultConfig(8000.0, DEFAULT_WINDOW_LENGHT,
						DEFAULT_WINDOW_OVERLAP);
		Map<String, ExtractorParam> params = new HashMap<String, ExtractorParam>();
		ExtractorReaderCtx readerCtx = createReader(extractorConfig, params,
				ExtractorEnum.ENERGY_EXTRACTOR,
				ExtractorEnum.LOUDNESS_EXTRACTOR,
				ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
				ExtractorEnum.WAVFORM_EXTRACTOR, ExtractorEnum.MFCC_EXTRACTOR);
		return readerCtx;
	}

	public static ExtractorReaderCtx createReader(
			IExtractorConfig extractorConfig,
			Map<String, ExtractorParam> params, ExtractorEnum... extractors) {
		ExtractorInputReader reader = new ExtractorInputReader();
		ExtractMarkerOnlineSegmentatorListener segmentatorListener = new ExtractMarkerOnlineSegmentatorListener();
		ExtractorReaderCtx readerCtx = new ExtractorReaderCtx(reader,
				segmentatorListener);

		reader.setConfig(extractorConfig);

		for (ExtractorEnum extractor : extractors) {
			IClassifier aClassifier = ExtractorUtils.registerThreshold(reader,
					extractor, null, ClassifierEnum.offline);
			if (aClassifier != null) {
				aClassifier.addClassificationListener(segmentatorListener);

				ExtractorParam param = ExtractorParamUtils.getSafeParam(params,
						extractor.name());
				Number threasholdCoef = ExtractorParamUtils.<Double> getValue(
						param,
						ExtractorParamUtils.commonParam.threasholdCoef.name(),
						1.1D);
				if (aClassifier instanceof AbstractThreshold) {
					((AbstractThreshold) aClassifier).setCoef(threasholdCoef
							.doubleValue());
				}
			}
		}

		return readerCtx;
	}

	public static IExtractorConfig createConfig(SignalFormat format) {
		return AndroidExtractorConfigUtil.defaultConfig(format.getSampleRate());
	}

	public static IExtractorInputReader createNormalizedReader() {
		return new ExtractorInputReader();
	}

	public static FFTExtractor createFftExtractor() {
		return new FFTExtractorCached();
	}

}
